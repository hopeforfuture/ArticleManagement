package com.src.articleservice.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class ArticleRequest {

    @NotBlank(message = "Article title is required")
    @Size(
            min = 3,
            max = 250,
            message = "Title must be between 3 and 250 characters"
    )
    private String title;

    @NotBlank(message = "Article body is required")
    private String body;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Set<String> tags;
}