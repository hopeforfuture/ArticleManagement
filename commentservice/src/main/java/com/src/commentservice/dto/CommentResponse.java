package com.src.commentservice.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommentResponse {

    private Long id;

    private String commentBody;

    private Long articleId;

    private Long userId;

    private Long parentId;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CommentResponse> replies =
            new ArrayList<>();
}