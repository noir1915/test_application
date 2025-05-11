package com.example.test_application.controller;

import com.example.test_application.config.AuthRequest;
import com.example.test_application.config.AuthResponse;
import com.example.test_application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        String token = userService.authenticate(authRequest);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}