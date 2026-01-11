package com.spicy.backend.user.dto.response;

public record UserHQViewResponse(
        Long userId,
        String name,
        String email,
        String role,
        String status
) {}
