package com.example.LogisticAggregator.Configuration;

import com.example.LogisticAggregator.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().startsWith("/ws") || request.getServletPath().startsWith("/socket.io") || request.getServletPath().equals("/user-role") || request.getServletPath().equals("/user/login")) {
            System.out.println("No JWT");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        String jwtToken = null;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            if(request.getCookies() != null) {
                jwtToken = Arrays.stream(request.getCookies())
                        .filter(cookie -> "authtoken".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);

                if(jwtToken == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
            } else {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if(jwtToken == null)
            jwtToken = authHeader.substring(7);

        System.out.println("JWT TOKEN: " + jwtToken);
        final String email = jwtService.extractEmail(jwtToken);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(email != null && authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if(jwtService.isValidToken(jwtToken, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                         userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
