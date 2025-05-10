package com.example.test_application.controller;

import com.example.test_application.config.AuthRequest;
import com.example.test_application.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        String token = authenticationService.authenticate(authRequest);
        return ResponseEntity.ok(token);
    }
}
