package com.example.LogisticAggregator.Controller;


import com.example.LogisticAggregator.Service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = "${frontend.url}")
public class HomeController {

    private final JwtService jwtService;

    HomeController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping
    public String Welcome() {
        return "Welcome!";
    }

    @GetMapping("/user-role")
    public Claims getCurrentUsersRole(@CookieValue("authtoken") String token) {
        Claims claims = jwtService.extractClaims(token);
        return claims;
    }
}
