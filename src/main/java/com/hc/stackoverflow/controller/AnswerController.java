package com.hc.stackoverflow.controller;

import com.hc.stackoverflow.entity.AnswerEntity;
import com.hc.stackoverflow.entity.dto.param.AnswerRequestDto;
import com.hc.stackoverflow.security.JwtUtil;
import com.hc.stackoverflow.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Tag(name = "Answer Management")
@SecurityRequirement(name = "bearerAuth")
public class AnswerController {
    private final AnswerService answerService;
    private final JwtUtil jwtUtil;

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new answer")
    public ResponseEntity<AnswerEntity> createAnswer(@Valid @RequestBody AnswerRequestDto param) {
        return ResponseEntity.ok(answerService.createAnswer(param));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get answer by ID")
    public ResponseEntity<AnswerEntity> getAnswer(@PathVariable Long id) {
        return answerService.getAnswerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/question/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all answers for a question")
    public ResponseEntity<?> getAnswersByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestionId(questionId));
    }

    @GetMapping(path = "/question/{questionId}/top", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get top answers for a question")
    public ResponseEntity<Page<AnswerEntity>> getTopAnswersByQuestion(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(answerService.getTopAnswersByQuestionId(questionId, page, size));
    }

    @PostMapping(path = "/{id}/accept", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark an answer as accepted")
    public ResponseEntity<AnswerEntity> acceptAnswer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromToken(token);
        return ResponseEntity.ok(answerService.markAsAccepted(id, userId));
    }

    @PostMapping(path = "/{id}/vote-up", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote up an answer")
    public ResponseEntity<AnswerEntity> voteUpAnswer(@PathVariable Long id) {
        return ResponseEntity.ok(answerService.updateAnswerVotes(id, 1));
    }

    @PostMapping(path = "/{id}/vote-down", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vote down an answer")
    public ResponseEntity<AnswerEntity> voteDownAnswer(@PathVariable Long id) {
        return ResponseEntity.ok(answerService.updateAnswerVotes(id, -1));
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete an answer")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.ok().build();
    }

}
