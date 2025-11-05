package com.rio_backend.post_service.service;

import com.rio_backend.post_service.dto.PostRequest;
import com.rio_backend.post_service.dto.PostResponse;
import com.rio_backend.post_service.enums.Category;
import com.rio_backend.post_service.model.Comment;
import com.rio_backend.post_service.model.Post;
import com.rio_backend.post_service.repository.CommentRepository;
import com.rio_backend.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostResponse addPost(PostRequest postRequest) {
        Post post = Post.builder()
                .authorId(postRequest.getAuthorId())
                .username(postRequest.getUsername())
                .content(postRequest.getContent())
                .category(postRequest.getCategory())
                .likes(0)
                .commentCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        postRepository.save(post);
        log.info("Post {} is saved", post.getId());
        return mapToPostResponse(post, new ArrayList<>());
    }

    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> mapToPostResponse(post, commentRepository.findByPostId(post.getId())))
                .toList();
    }

    public List<PostResponse> getUserPosts(String authorId) {
        if (authorId == null || authorId.isBlank()) {
            throw new IllegalArgumentException("authorId cannot be null or empty");
        }

        log.info("Fetching posts by authorId: {}", authorId);

        return postRepository.findByAuthorId(authorId)
                .stream()
                .map(post -> mapToPostResponse(post, commentRepository.findByPostId(post.getId())))
                .toList();
    }

    public List<PostResponse> getPostsByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Invalid category");
        }

        log.info("Fetching posts by category: {}", category);

        return postRepository.findByCategory(category)
                .stream()
                .map(post -> mapToPostResponse(post, commentRepository.findByPostId(post.getId())))
                .toList();
    }

    public void deletePost(String postId, String authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        if (!post.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("You are not authorized to delete this post.");
        }

        postRepository.delete(post);
        log.info("Post deleted successfully for author {}", authorId);
    }

    public void updateLikes(String postId, boolean isLike) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        int likes = post.getLikes();

        if (isLike) post.setLikes(likes + 1);
        else if (likes > 0) post.setLikes(likes - 1);

        post.setUpdatedAt(Instant.now());
        postRepository.save(post);
        log.info("{} post {} -> total likes: {}", isLike ? "Liked" : "Unliked", postId, post.getLikes());
    }

    private PostResponse mapToPostResponse(Post post, List<Comment> comments) {
        return PostResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .username(post.getUsername())
                .content(post.getContent())
                .category(post.getCategory())
                .likes(post.getLikes())
                .commentCount(post.getCommentCount())
                .comments(comments)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
