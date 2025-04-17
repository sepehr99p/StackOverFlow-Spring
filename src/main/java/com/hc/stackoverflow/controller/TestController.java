package com.hc.stackoverflow.controller;

import com.hc.stackoverflow.entity.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> test() {
        return ResponseEntity.ok(new ApiResponse("Test endpoint working!", "SUCCESS"));
    }

    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> hello() {
        return ResponseEntity.ok(new ApiResponse("Hello World!", "SUCCESS"));
    }
}