package com.spicy.backend.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d|.*\\W).{8,20}$|^(?=.*\\d)(?=.*\\W).{8,20}$",
                message = "비밀번호는 8자 이상 20자 이하이며, 영문 대소문자, 숫자, 특수문자 중 2가지 이상을 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        String username,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
}
