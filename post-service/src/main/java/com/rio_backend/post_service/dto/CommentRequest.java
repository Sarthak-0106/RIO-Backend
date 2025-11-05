package com.rio_backend.post_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    private String postId;
    private String authorId;
    private String username;
    private String content;
    private String parentCommentId;
}
