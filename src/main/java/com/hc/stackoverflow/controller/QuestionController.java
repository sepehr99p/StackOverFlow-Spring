package com.hc.stackoverflow.controller;

import com.hc.stackoverflow.entity.QuestionEntity;
import com.hc.stackoverflow.entity.dto.param.QuestionRequestDto;
import com.hc.stackoverflow.entity.dto.response.ApiResponse;
import com.hc.stackoverflow.entity.dto.response.QuestionResponseDto;
import com.hc.stackoverflow.exception.QuestionCreationException;
import com.hc.stackoverflow.exception.ResourceNotFoundException;
import com.hc.stackoverflow.security.JwtUtil;
import com.hc.stackoverflow.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question Management")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {
    private final QuestionService questionService;
    private final JwtUtil jwtUtil;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working!");
    }

    @Autowired
    public QuestionController(QuestionService questionService, JwtUtil jwtUtil) {
        this.questionService = questionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new question")
    public ResponseEntity<ApiResponse<QuestionResponseDto>> createQuestion(
            @RequestBody QuestionRequestDto param) {
        try {
            QuestionResponseDto createdQuestion = questionService.createQuestion(param);
            return ResponseEntity.ok(
                    ApiResponse.success(
                            createdQuestion,
                            "Question created successfully"
                    )
            );
        } catch (QuestionCreationException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage(), "QUESTION_CREATION_ERROR"));
        }
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get question by ID")
    public ResponseEntity<QuestionEntity> getQuestion(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all questions")
    public ResponseEntity<List<QuestionEntity>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping(path = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my questions")
    public ResponseEntity<List<QuestionEntity>> getMyQuestions(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromToken(token);
        return ResponseEntity.ok(questionService.getQuestionsByUserId(userId));
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search questions")
    public ResponseEntity<Page<QuestionEntity>> searchQuestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionService.searchQuestions(query, page, size));
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete a question")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromToken(token);
        questionService.checkQuestionOwnership(id, userId);
        questionService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update a question")
    public ResponseEntity<QuestionEntity> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionEntity question,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromToken(token);
        questionService.checkQuestionOwnership(id, userId);
        return ResponseEntity.ok(questionService.updateQuestion(id, question));
    }

    @PostMapping(path = "/{id}/vote-up", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote up a question")
    public ResponseEntity<QuestionEntity> voteUpQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.updateQuestionVotes(id, 1));
    }

    @PostMapping(path = "/{id}/vote-down", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote down a question")
    public ResponseEntity<QuestionEntity> voteDownQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.updateQuestionVotes(id, -1));
    }
}
