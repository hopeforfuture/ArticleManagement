package com.src.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String avatarName;
    private String avatarType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer status;
}