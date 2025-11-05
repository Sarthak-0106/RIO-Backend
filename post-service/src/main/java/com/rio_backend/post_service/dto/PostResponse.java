package com.rio_backend.post_service.dto;

import com.rio_backend.post_service.enums.Category;
import com.rio_backend.post_service.model.Comment;
import com.rio_backend.post_service.model.Post;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private String id;

    private  String authorId;
    private String username;
    private String content;
    private Category category;

    private int likes;
    private int commentCount;

    private Instant createdAt;
    private Instant updatedAt;

    private List<Comment> comments;
}
