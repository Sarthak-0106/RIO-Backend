package com.rio_backend.post_service.service;

import com.rio_backend.post_service.dto.CommentRequest;
import com.rio_backend.post_service.model.Comment;
import com.rio_backend.post_service.model.Post;
import com.rio_backend.post_service.repository.CommentRepository;
import com.rio_backend.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GraphLookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final MongoTemplate mongoTemplate;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment addComment(CommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + request.getPostId()));

        Comment newComment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .postId(request.getPostId())
                .authorId(request.getAuthorId())
                .username(request.getUsername())
                .content(request.getContent())
                .createdAt(Instant.now())
                .replies(new ArrayList<>())
                .build();

        if (request.getParentCommentId() == null) {
            // Add as top-level comment
            commentRepository.save(newComment);
        } else {
            // Add as a reply
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found with ID: " + request.getParentCommentId()));

            if (parentComment.getReplies() == null) {
                parentComment.setReplies(new ArrayList<>());
            }

            newComment.setParentCommentId(request.getParentCommentId());
            commentRepository.save(newComment);
        }

        // update post comment count
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        log.info("Added {} under post {}",
                request.getParentCommentId() == null ? "comment" : "reply", post.getId());

        return newComment;
    }

//    public List<Comment> getCommentsByPostId(String postId) {
//        GraphLookupOperation graphLookup = GraphLookupOperation.builder()
//                .from("comments")
//                .startWith("$id") // 👈 use "$id" not "$_id"
//                .connectFrom("id") // 👈 note: connectFrom() not connectFromField()
//                .connectTo("parentCommentId")
//                .as("replies");
//
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.match(Criteria.where("postId").is(postId).and("parentCommentId").is(null)),
//                graphLookup,
//                Aggregation.sort(org.springframework.data.domain.Sort.by("createdAt"))
//        );
//
//        AggregationResults<Comment> results = mongoTemplate.aggregate(aggregation, "comments", Comment.class);
//        return results.getMappedResults();
//    }

    // added tree structure to be fetched
    public List<Comment> getCommentsByPostId(String postId) {
        List<Comment> allComments = commentRepository.findByPostId(postId);

        Map<String, Comment> commentMap = allComments.stream()
                .collect(Collectors.toMap(Comment::getId, c -> c));

        List<Comment> rootComments = new ArrayList<>();

        for (Comment comment : allComments) {
            if (comment.getParentCommentId() != null) {
                Comment parent = commentMap.get(comment.getParentCommentId());
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>());
                    }
                    parent.getReplies().add(comment);
                }
            } else {
                rootComments.add(comment);
            }
        }

        return rootComments;
    }


    private void populateReplies(Comment comment) {
        List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());
        if (replies.isEmpty()) return;

        comment.setReplies(replies);
        for (Comment reply : replies) {
            populateReplies(reply); // recursion for nested replies
        }
    }

    public void deleteComment(String postId, String commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));

        commentRepository.delete(comment);
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);

        log.info("Deleted comment {} from post {}", commentId, postId);
    }
}
