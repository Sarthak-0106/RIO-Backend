package com.rio_backend.auth_service.controller;

import com.rio_backend.auth_service.dto.UserRequest;
import com.rio_backend.auth_service.dto.UserResponse;
import com.rio_backend.auth_service.model.User;
import com.rio_backend.auth_service.service.JwtService;
import com.rio_backend.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest userRequest) {
        UserResponse response = userService.registerUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        try {
            UserResponse response = userService.loginUser(userRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        return userService.refresh(request);
    }
}
