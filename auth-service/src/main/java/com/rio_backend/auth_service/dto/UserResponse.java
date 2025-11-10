package com.rio_backend.auth_service.dto;


import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String email;
    private String username;
    private String authorId;
    private String accessToken;
}
