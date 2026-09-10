package com.src.articleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ArticleResponse {

    private Long id;

    private String title;

    private String body;

    private String imageName;

    private String imageType;

    private Long categoryId;

    private Long userId;

    private Boolean isPublished;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}