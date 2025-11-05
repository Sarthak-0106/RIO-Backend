package com.rio_backend.post_service.repository;

import com.rio_backend.post_service.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    List<Comment> findByParentCommentId(String parentCommentId);
}
