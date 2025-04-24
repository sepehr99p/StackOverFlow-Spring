package com.hc.stackoverflow.entity.dto.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotBlank(message = "Description is required")
    private String Description;

    @NotBlank(message = "ParentId is required")
    private String ParentId;
}
