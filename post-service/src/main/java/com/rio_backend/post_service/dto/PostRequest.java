package com.rio_backend.post_service.dto;

import com.rio_backend.post_service.enums.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {
    private String authorId;
    private String username;
    private String content;
    private Category category;
}
