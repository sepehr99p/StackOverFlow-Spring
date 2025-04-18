package com.hc.stackoverflow.entity.dto.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerRequestDto {

    @NotBlank(message = "Description is required")
    private String Description;

    @NotBlank(message = "QuestionId is required")
    private Long QuestionId;

}
