package com.example.LogisticAggregator.Service;

import com.example.LogisticAggregator.DAO.AdminRepository;
import com.example.LogisticAggregator.Model.Admin;
import com.example.LogisticAggregator.Model.AppUser;
import io.jsonwebtoken.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService implements AppUserService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;



    AdminService(AdminRepository adminRepository, JwtService jwtService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager) {
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String createAdmin(Admin admin) {
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return "New admin created successfully";
    }

    public ResponseEntity<String> verify(Admin admin) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        admin.getEmail(), admin.getPassword()
                )
        );
        if(authenticate.isAuthenticated()) {
            Long id = findByEmail(admin.getEmail()).getId();
            String jwtToken = jwtService.generateToken(admin.getEmail(), id, "ADMIN");

            ResponseCookie cookie = ResponseCookie.from("authtoken", jwtToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body("LOGIN SUCCESSFUL!");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username and password");
    }

    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

}
