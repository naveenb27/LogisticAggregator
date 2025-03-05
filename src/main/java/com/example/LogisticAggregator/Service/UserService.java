package com.example.LogisticAggregator.Service;

import com.example.LogisticAggregator.DAO.ShipmentDetailsRepository;
import com.example.LogisticAggregator.DAO.UserRepository;
import com.example.LogisticAggregator.Model.AppUser;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import com.example.LogisticAggregator.Model.User;
import com.example.LogisticAggregator.Utils.CustomAdminDetails;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Primary
public class UserService implements AppUserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ShipmentDetailsRepository shipmentDetailsRepository;

    UserService(UserRepository userRepository, ShipmentDetailsRepository shipmentDetailsRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.shipmentDetailsRepository = shipmentDetailsRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    public String createUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "User created successfully!";
    }

    public ResponseEntity<String> verify(AppUser user) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );

        System.out.println(authenticate);

        String authority = String.valueOf(authenticate.getAuthorities().stream().toList().get(0));

        System.out.println("Authority: " + authority);
        System.out.println("Is authenticated: "+ authenticate.isAuthenticated());
        if(authenticate.isAuthenticated()){
            CustomAdminDetails a  =(CustomAdminDetails) authenticate.getPrincipal();

            Long id = a.Id();

            System.out.println(id);

            String jwtToken = jwtService.generateToken(user.getEmail(), a.Id(), authority);
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


    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<ShipmentDetails> trackMyOrder(String token) {
        List<ShipmentDetails> shipmentDetails = shipmentDetailsRepository.findBySender_Id(findByEmail(jwtService.extractEmail(token)).getId());
//        shipmentDetails.getSender().getId();
//        shipmentDetails.getLogisticProvider.getId();

        return shipmentDetails;
    }
}
