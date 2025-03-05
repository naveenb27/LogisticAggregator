package com.example.LogisticAggregator.Service;


import com.example.LogisticAggregator.Model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String JwtSecret;

    public String generateToken(String email, Long id, String role) {
        HashMap<String, Object> claims = new HashMap<>();

        claims.put("email", email);
        claims.put("roles", role);
        claims.put("id", id);

        String JwtToken = Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
                .and()
                .signWith(generateKey())
                .compact();


        return JwtToken;
    }

    public SecretKey generateKey() {
        return Keys.hmacShaKeyFor(JwtSecret.getBytes());
    }

    public boolean isValidToken(String jwtToken, UserDetails userDetails) {
        final String email  = extractEmail(jwtToken);

        return (email.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken).getExpiration();
    }

    public String extractEmail(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    public Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("roles", String.class);
    }

    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimResolver) {
         Claims claims = extractClaims(jwtToken);

         return claimResolver.apply(claims);
    }
}
