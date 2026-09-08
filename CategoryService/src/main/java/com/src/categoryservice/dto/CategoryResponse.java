package com.src.categoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}