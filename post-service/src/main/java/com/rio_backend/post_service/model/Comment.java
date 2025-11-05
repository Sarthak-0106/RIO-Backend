package com.rio_backend.post_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    private String id;
    private String postId;

    private String authorId;
    private String username;
    private String content;

    private String parentCommentId;
    private List<Comment> replies;

    private Instant createdAt;
}

