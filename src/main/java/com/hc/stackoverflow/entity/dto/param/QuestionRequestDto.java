package com.hc.stackoverflow.entity.dto.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionRequestDto {
    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String Description;

}
