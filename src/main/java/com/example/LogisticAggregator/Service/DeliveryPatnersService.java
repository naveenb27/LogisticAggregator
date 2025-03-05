package com.example.LogisticAggregator.Service;

import com.example.LogisticAggregator.DAO.DeliveryPartnersRepository;
import com.example.LogisticAggregator.Model.DeliveryPartners;
import com.example.LogisticAggregator.Model.LogisticProvider;
import com.example.LogisticAggregator.Utils.CustomAdminDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPatnersService {

    private final DeliveryPartnersRepository deliveryPartnersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    DeliveryPatnersService(DeliveryPartnersRepository deliveryPartnersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.deliveryPartnersRepository = deliveryPartnersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public String createDeliveryPatner(DeliveryPartners deliveryPartners) {
        deliveryPartners.setPassword(bCryptPasswordEncoder.encode(deliveryPartners.getPassword()));
        deliveryPartnersRepository.save(deliveryPartners);
        return "DELIVERY PARTNER CREATED SUCCESSFULLY!";
    }


    public ResponseEntity<String> verify(DeliveryPartners deliveryPartners) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(deliveryPartners.getEmail(), deliveryPartners.getPassword())
        );

        String authority = String.valueOf(authenticate.getAuthorities().stream().toList().get(0));

        if(authenticate.isAuthenticated()){
            CustomAdminDetails a  =(CustomAdminDetails) authenticate.getPrincipal();

            Long id = a.Id();

            System.out.println(id);

            String jwtToken = jwtService.generateToken(deliveryPartners.getEmail(), a.Id(), authority);
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



    public DeliveryPartners findByEmail(String email) {
        return deliveryPartnersRepository.findByEmail(email);
    }

    public DeliveryPartners findById(Long id) {
        return deliveryPartnersRepository.findById(id).orElseThrow(() -> new RuntimeException("Error"));
    }
}
