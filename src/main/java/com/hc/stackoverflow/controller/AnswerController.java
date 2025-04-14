package com.hc.stackoverflow.controller;

import com.hc.stackoverflow.entity.AnswerEntity;
import com.hc.stackoverflow.security.JwtUtil;
import com.hc.stackoverflow.service.AnswerService;
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
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Tag(name = "Answer Management")
@SecurityRequirement(name = "bearerAuth")
public class AnswerController {
    private final AnswerService answerService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new answer")
    public ResponseEntity<AnswerEntity> createAnswer(@RequestBody AnswerEntity answer) {
        return ResponseEntity.ok(answerService.createAnswer(answer));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get answer by ID")
    public ResponseEntity<AnswerEntity> getAnswer(@PathVariable Long id) {
        return answerService.getAnswerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Get all answers for a question")
    public ResponseEntity<List<AnswerEntity>> getAnswersByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestionId(questionId));
    }

    @GetMapping("/question/{questionId}/top")
    @Operation(summary = "Get top answers for a question")
    public ResponseEntity<Page<AnswerEntity>> getTopAnswersByQuestion(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(answerService.getTopAnswersByQuestionId(questionId, page, size));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark an answer as accepted")
    public ResponseEntity<AnswerEntity> acceptAnswer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromToken(token);
        return ResponseEntity.ok(answerService.markAsAccepted(id, userId));
    }

    @PostMapping("/{id}/vote-up")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote up an answer")
    public ResponseEntity<AnswerEntity> voteUpAnswer(@PathVariable Long id) {
        return ResponseEntity.ok(answerService.updateAnswerVotes(id, 1));
    }

    @PostMapping("/{id}/vote-down")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote down an answer")
    public ResponseEntity<AnswerEntity> voteDownAnswer(@PathVariable Long id) {
        return ResponseEntity.ok(answerService.updateAnswerVotes(id, -1));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete an answer")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.ok().build();
    }

}
