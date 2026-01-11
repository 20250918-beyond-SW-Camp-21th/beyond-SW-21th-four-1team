package com.spicy.backend.user.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
