package com.hc.stackoverflow.controller;

import com.hc.stackoverflow.entity.Question;
import com.hc.stackoverflow.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question Management")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new question")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        return ResponseEntity.ok(questionService.createQuestion(question));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my questions")
    public ResponseEntity<List<Question>> getMyQuestions(@RequestHeader("Authorization") String token) {
        // Extract userId from token
        Long userId = extractUserIdFromToken(token);
        return ResponseEntity.ok(questionService.getQuestionsByUserId(userId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search questions")
    public ResponseEntity<Page<Question>> searchQuestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionService.searchQuestions(query, page, size));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete a question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/vote-up")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote up a question")
    public ResponseEntity<Question> voteUpQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.updateQuestionVotes(id, 1));
    }

    @PostMapping("/{id}/vote-down")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote down a question")
    public ResponseEntity<Question> voteDownQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.updateQuestionVotes(id, -1));
    }

    private Long extractUserIdFromToken(String token) {
        // Implement token extraction logic
        return 1L; // Placeholder
    }
}
