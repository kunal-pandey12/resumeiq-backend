package com.ResumeIQ.controller;


import com.ResumeIQ.dto.UserDto;
import com.ResumeIQ.entity.User;
import com.ResumeIQ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register API
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDto userDTO) {
        User savedUser = userService.registerUser(userDTO);
        return ResponseEntity.ok(savedUser);
    }

    // Get User by Email
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}