package com.ResumeIQ.securityconfig;

import com.ResumeIQ.service.JwtFilter;
import com.ResumeIQ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Yeh class poori security ki setting karti hai
@Configuration
public class SecurityConfig {

    // Har request pe JWT check karne wala filter
    @Autowired
    private JwtFilter jwtFilter;

    // User dhundne ke liye — login ke time kaam aata hai
    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API me CSRF attack nahi hota, isliye disable kiya
       // REST API mein CSRF attack ka darr nahi hota kyunki browser cookies use nahi karta, isliye band kar diya
      //  STATELESS — Server koi session save nahi karega — har request mein token aayega, wahi check hoga

                .csrf(csrf -> csrf.disable())

                // Server koi session nahi rakhega — har baar token se verify hoga
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Register aur Login ke liye token ki zaroorat nahi
                        .requestMatchers("/api/auth/**").permitAll()
                        // Baaki sab APIs ke liye token mandatory hai
                        .anyRequest().authenticated())

                // Har request mein pehle JWT check hoga, tab aage jayegi
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Login ke time email + password verify karta hai
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}