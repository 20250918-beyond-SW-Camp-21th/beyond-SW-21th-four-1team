package com.spicy.backend.user.dto.response;

import com.spicy.backend.user.domain.User;
import com.spicy.backend.user.enums.UserRole;

public record UserHQViewResponse(
        Long userId,
        String loginId,
        String username,
        String email,
        UserRole userRole
) {
    public static UserHQViewResponse from(User user) {
        return new UserHQViewResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole()
        );
    }
}
