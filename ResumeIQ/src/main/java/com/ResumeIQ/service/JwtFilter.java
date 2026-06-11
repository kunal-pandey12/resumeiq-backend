package com.ResumeIQ.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// Har request pe ek baar chalega — token check karega
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Request ke header se token nikalo
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        // Token "Bearer xxxxx" format mein aata hai
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " hata ke token lo
            email = jwtUtil.extractEmail(token); // token se email nikalo
        }

        // Agar email mila aur abhi login nahi hai security context mein
        if (email != null && SecurityContextHolder.getContext()
                .getAuthentication() == null) {

            // Database se user dhundo
            UserDetails userDetails =
                    userService.loadUserByUsername(email);

            // Token valid hai toh user ko authenticate karo
            if (jwtUtil.isTokenValid(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null,
                                userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                // Spring Security ko batao yeh user authenticated hai
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        // Aage jane do request ko
        filterChain.doFilter(request, response);
    }
}