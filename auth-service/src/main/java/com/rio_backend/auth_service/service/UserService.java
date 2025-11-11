package com.rio_backend.auth_service.service;

import com.rio_backend.auth_service.dto.UserRequest;
import com.rio_backend.auth_service.dto.UserResponse;
import com.rio_backend.auth_service.model.User;
import com.rio_backend.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public UserResponse registerUser(UserRequest userRequest) {

        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
        long nextId = userRepository.count() + 1;
        String generatedUsername = "User" + nextId;

        String accessToken = jwtService.generateAccessToken(userRequest.getEmail());
        String refreshToken = jwtService.generateRefreshToken(userRequest.getEmail());

        User user = User.builder()
                .email(userRequest.getEmail())
                .password(hashedPassword)
                .username(generatedUsername)
                .authorId(UUID.randomUUID().toString())
                .refreshToken(refreshToken)
                .build();

        userRepository.save(user);

        return UserResponse.builder()
                .email(user.getEmail())
                .authorId(user.getAuthorId())
                .username(user.getUsername())
                .accessToken(accessToken)
                .build();
    }

    public UserResponse loginUser(UserRequest userRequest) {
        User user = userRepository.findByEmail(userRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!passwordEncoder.matches(userRequest.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Wrong Password");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .authorId(user.getAuthorId())
                .accessToken(accessToken)
                .build();
    }

    public ResponseEntity<?> refresh(Map<String, String> request) {
        String email = request.get("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String refreshToken = user.getRefreshToken();
        if (!jwtService.validateToken(refreshToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");

        String newAccessToken = jwtService.generateAccessToken(email);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
