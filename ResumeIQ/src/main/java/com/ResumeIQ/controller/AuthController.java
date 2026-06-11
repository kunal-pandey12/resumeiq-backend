package com.ResumeIQ.controller;

import com.ResumeIQ.dto.UserDto;
import com.ResumeIQ.entity.User;
import com.ResumeIQ.service.JwtUtil;
import com.ResumeIQ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

// Auth ke liye — Register aur Login yahan hoga
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired   // UserService — register karna aur user dhundna
    private UserService userService;

    @Autowired    // JwtUtil — token banana aur validate karna
    private JwtUtil jwtUtil;

    @Autowired
// AuthenticationManager — email/password sahi hai ya nahi verify karna
    private AuthenticationManager authenticationManager;

    // Register — naya user banao
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        User user = userService.registerUser(userDto);
        return ResponseEntity.ok("User registered: " + user.getName());
    }

    // Login — email/password check karo, token do
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {

        // Email aur password verify karo
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                )
        );

        // Sahi hai toh token banao aur bhejo
        String token = jwtUtil.generateToken(userDto.getEmail());
        return ResponseEntity.ok("Bearer " + token);
    }
}