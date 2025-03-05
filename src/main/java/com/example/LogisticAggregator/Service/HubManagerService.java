package com.example.LogisticAggregator.Service;


import com.example.LogisticAggregator.DAO.HubManagerRepository;
import com.example.LogisticAggregator.DAO.HubRepository;
import com.example.LogisticAggregator.Model.DeliveryPartners;
import com.example.LogisticAggregator.Model.Hub;
import com.example.LogisticAggregator.Model.HubManager;
import com.example.LogisticAggregator.Utils.CustomAdminDetails;
import io.jsonwebtoken.Jwt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HubManagerService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final HubManagerRepository hubManagerRepository;
    private final HubRepository hubRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    HubManagerService(HubManagerRepository hubManagerRepository, JwtService jwtService, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder,HubRepository hubRepository) {
        this.hubManagerRepository = hubManagerRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.hubRepository = hubRepository;
    }

    public Long getHubIdById(Long hubId) {
        HubManager hubManager = hubManagerRepository.findById(hubId).orElseThrow(() -> new RuntimeException("Error"));
        return hubManager.getHub().getId();
    }

    public HubManager assignManagerToHub(Long hubId, HubManager hubManager) {
        Hub hub = hubRepository.findById(hubId).orElseThrow(() -> new RuntimeException("ERROR"));

        hubManager.setHub(hub);
        hubManager.setPassword(bCryptPasswordEncoder.encode(hubManager.getPassword()));
        return hubManagerRepository.save(hubManager);
    }

    public ResponseEntity<String> verify(HubManager hubManager) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(hubManager.getEmail(), hubManager.getPassword())
        );

        String authority = String.valueOf(authenticate.getAuthorities().stream().toList().get(0));

        if(authenticate.isAuthenticated()){
            CustomAdminDetails a  =(CustomAdminDetails) authenticate.getPrincipal();

            Long id = a.Id();

            System.out.println(id);

            String jwtToken = jwtService.generateToken(hubManager.getEmail(), a.Id(), authority);
            ResponseCookie cookie = ResponseCookie.from("authtoken", jwtToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();


            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(jwtToken);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username and password");
    }

    public List<HubManager> getAllHubManager() {
        return hubManagerRepository.findAll();
    }
}
