package com.hc.stackoverflow.controller;

import com.hc.stackoverflow.entity.AnswerCommentEntity;
import com.hc.stackoverflow.entity.CommentEntity;
import com.hc.stackoverflow.entity.QuestionCommentEntity;
import com.hc.stackoverflow.entity.UserEntity;
import com.hc.stackoverflow.entity.dto.param.CommentRequestDto;
import com.hc.stackoverflow.entity.dto.response.ApiResponse;
import com.hc.stackoverflow.exception.ResourceNotFoundException;
import com.hc.stackoverflow.repository.UserRepository;
import com.hc.stackoverflow.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Management")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    @PostMapping("/api/questions/{questionId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuestionCommentEntity>> createQuestionComment(
            @PathVariable Long questionId,
            @Valid @RequestBody CommentRequestDto request) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        QuestionCommentEntity comment = commentService.createQuestionComment(
            questionId,
            request.getDescription(),
            user.getId()
        );

        return ResponseEntity.ok(ApiResponse.success(comment, "Comment created successfully"));
    }

    @PostMapping("/api/answers/{answerId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AnswerCommentEntity>> createAnswerComment(
            @PathVariable Long answerId,
            @Valid @RequestBody CommentRequestDto request) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AnswerCommentEntity comment = commentService.createAnswerComment(
            answerId,
            request.getDescription(),
            user.getId()
        );

        return ResponseEntity.ok(ApiResponse.success(comment, "Comment created successfully"));
    }

    @GetMapping("/api/questions/{questionId}/comments")
    public ResponseEntity<ApiResponse<List<QuestionCommentEntity>>> getQuestionComments(
            @PathVariable Long questionId) {
        List<QuestionCommentEntity> comments = commentService.getQuestionComments(questionId);
        return ResponseEntity.ok(ApiResponse.success(comments, "Comments retrieved successfully"));
    }

    @GetMapping("/api/answers/{answerId}/comments")
    public ResponseEntity<ApiResponse<List<AnswerCommentEntity>>> getAnswerComments(
            @PathVariable Long answerId) {
        List<AnswerCommentEntity> comments = commentService.getAnswerComments(answerId);
        return ResponseEntity.ok(ApiResponse.success(comments, "Comments retrieved successfully"));
    }

    @GetMapping(path = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get comment by ID")
    public ResponseEntity<CommentEntity> getComment(@PathVariable Long id) {
        return commentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/question/{questionId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all comments for a question")
    public ResponseEntity<List<CommentEntity>> getCommentsByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(commentService.getCommentsByQuestionId(questionId));
    }

    @GetMapping(path = "/answer/{answerId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all comments for an answer")
    public ResponseEntity<List<CommentEntity>> getCommentsByAnswer(@PathVariable Long answerId) {
        return ResponseEntity.ok(commentService.getCommentsByAnswerId(answerId));
    }

    @GetMapping(path = "/user/{userId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all comments by a user")
    public ResponseEntity<List<CommentEntity>> getCommentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }

    @GetMapping(path = "/question/{questionId}/page",produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get paginated comments for a question")
    public ResponseEntity<Page<CommentEntity>> getCommentsByQuestion(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByQuestionId(questionId, page, size));
    }

    @GetMapping(path = "/answer/{answerId}/page",produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get paginated comments for an answer")
    public ResponseEntity<Page<CommentEntity>> getCommentsByAnswer(
            @PathVariable Long answerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByAnswerId(answerId, page, size));
    }

    @DeleteMapping(path = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
