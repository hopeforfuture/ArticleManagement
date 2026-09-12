package com.src.commentservice.dto;

import lombok.Data;

@Data
public class ArticleResponse {

    private Long id;

    private String title;

    private Boolean isPublished;
}