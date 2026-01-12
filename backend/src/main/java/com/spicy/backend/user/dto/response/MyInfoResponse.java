package com.spicy.backend.user.dto.response;

import com.spicy.backend.user.domain.User;
import com.spicy.backend.user.enums.UserRole;

public record MyInfoResponse(
        Long userId,
        String loginId,
        String username,
        String email,
        UserRole userRole
) {
    public static MyInfoResponse from(User user) {
        return new MyInfoResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole()
        );
    }
}
