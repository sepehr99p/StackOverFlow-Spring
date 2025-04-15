package com.hc.stackoverflow.exception;


import com.hc.stackoverflow.entity.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        errorData.put("error", "NOT_FOUND");
        errorData.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(errorData));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResourceException(
            DuplicateResourceException ex,
            WebRequest request) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("status", String.valueOf(HttpStatus.CONFLICT.value()));
        errorData.put("error", "CONFLICT");
        errorData.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(errorData));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex,
            WebRequest request) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("status", String.valueOf(HttpStatus.CONFLICT.value()));
        errorData.put("error", "USER_EXISTS");
        errorData.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(errorData));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("status", String.valueOf(HttpStatus.FORBIDDEN.value()));
        errorData.put("error", "FORBIDDEN");
        errorData.put("message", "You don't have permission to perform this action");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(errorData));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("status", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        errorData.put("error", "UNAUTHORIZED");
        errorData.put("message", "Invalid credentials");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(errorData));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(
            Exception ex,
            WebRequest request) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errorData.put("error", "BAD_REQUEST");
        errorData.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorData));
    }
}
