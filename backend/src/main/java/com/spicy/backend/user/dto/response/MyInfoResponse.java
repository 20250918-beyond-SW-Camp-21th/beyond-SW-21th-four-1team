package com.spicy.backend.user.dto.response;

public record MyInfoResponse(
        Long userId,
        String loginId,
        String name,
        String email,
        String role
) {}
