package com.spicy.backend.user.api;

import com.spicy.backend.global.common.ApiResponse;
import com.spicy.backend.user.dto.request.LoginRequest;
import com.spicy.backend.user.dto.request.LogoutRequest;
import com.spicy.backend.user.dto.request.SignUpRequest;
import com.spicy.backend.user.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Operation(summary = "회원가입", description = "아이디, 비밀번호, 이름, 이메일을 입력해 회원가입한다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignUpRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 토큰을 발급한다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "로그아웃", description = "사용자의 refresh 토큰을 만료시켜 로그아웃 처리한다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
