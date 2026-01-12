package com.spicy.backend.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordRequest(
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String currentPassword,

        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d|.*\\W).{8,20}$|^(?=.*\\d)(?=.*\\W).{8,20}$",
                message = "비밀번호는 8자 이상 20자 이하이며, 영문 대소문자, 숫자, 특수문자 중 2가지 이상을 포함해야 합니다."
        )
        String newPassword
) {
}
