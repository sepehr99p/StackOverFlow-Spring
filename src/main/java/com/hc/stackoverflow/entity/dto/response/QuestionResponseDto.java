package com.hc.stackoverflow.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuestionResponseDto {
    private Long id;
    private String title;
    private String description;
    private String username;
    private LocalDateTime createdAt;
}
