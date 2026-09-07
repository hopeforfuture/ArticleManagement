package com.src.userservice.dto;

public record AuthResponse(
        boolean success,
        String message,
        String token,
        String username
) {
}