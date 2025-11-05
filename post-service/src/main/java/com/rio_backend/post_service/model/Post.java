package com.rio_backend.post_service.model;

import com.rio_backend.post_service.enums.Category;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    private String authorId;
    private String username;
    private String content;
    private Category category;

    private int likes;
    private int commentCount;

    private Instant createdAt;
    private Instant updatedAt;

    @Transient
    private List<?> comments;
}
