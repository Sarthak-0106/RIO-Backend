package com.rio_backend.post_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String email;
    private String password;
    private String userId;
    private String username;
    private Instant joinedAt;
    private String authorId;
    private String refreshToken;
}
