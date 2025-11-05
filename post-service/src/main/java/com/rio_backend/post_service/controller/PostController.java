package com.rio_backend.post_service.controller;

import com.rio_backend.post_service.dto.PostRequest;
import com.rio_backend.post_service.dto.PostResponse;
import com.rio_backend.post_service.enums.Category;
import com.rio_backend.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse addPost(@RequestBody PostRequest postRequest){
        return postService.addPost(postRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getAllPosts(){
        return postService.getAllPosts();
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserPosts(@RequestParam String authorId){
        try {
            return ResponseEntity.ok(postService.getUserPosts(authorId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getPostsByCategory(@RequestParam String category) {
        try{
            Category validCategory = Category.valueOf(category.toUpperCase());
            return ResponseEntity.ok(postService.getPostsByCategory(validCategory));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Invalid category",
                            "message", category + " is not a valid category"
                    ));
        }
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId, @RequestParam String authorId) {
        try {
            postService.deletePost(postId, authorId);
            return ResponseEntity.ok("Post deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{postId}/like")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> likePost(@PathVariable String postId) {
        try {
            postService.updateLikes(postId, true);
            return ResponseEntity.ok("Post liked successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{postId}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> dislikePost(@PathVariable String postId) {
        try {
            postService.updateLikes(postId, false);
            return ResponseEntity.ok("Post disliked successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
