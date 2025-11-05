package com.rio_backend.post_service.repository;

import com.rio_backend.post_service.enums.Category;
import com.rio_backend.post_service.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByAuthorId(String authorId);
    List<Post> findByCategory(Category category);
}
