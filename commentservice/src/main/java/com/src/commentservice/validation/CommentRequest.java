package com.src.commentservice.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment body is required")
    @Size(
            min = 1,
            max = 2000,
            message = "Comment must be between 1 and 2000 characters"
    )
    private String commentBody;

    @NotNull(message = "Article ID is required")
    private Long articleId;

    /**
     * null = new comment
     * value = reply to existing comment
     */
    private Long parentId;
}