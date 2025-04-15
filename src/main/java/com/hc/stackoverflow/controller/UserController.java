package com.hc.stackoverflow.controller;

import com.hc.stackoverflow.entity.UserEntity;
import com.hc.stackoverflow.exception.ResourceNotFoundException;
import com.hc.stackoverflow.security.JwtUtil;
import com.hc.stackoverflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserEntity> registerUser(@RequestBody UserEntity user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    public ResponseEntity<UserEntity> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by keyword")
    public ResponseEntity<List<UserEntity>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable Long id,
            @RequestBody UserEntity updatedUser,
            @RequestHeader("Authorization") String token) {
        Long currentUserId = jwtUtil.extractUserIdFromToken(token);
        userService.checkUserPermission(id, currentUserId);
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Deactivate a user account")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long currentUserId = jwtUtil.extractUserIdFromToken(token);
        userService.checkUserPermission(id, currentUserId);
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserEntity> getCurrentUser(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromToken(token);
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
