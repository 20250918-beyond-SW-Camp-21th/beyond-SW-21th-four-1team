package com.spicy.backend.user.api;

import com.spicy.backend.global.common.ApiResponse;
import com.spicy.backend.user.dto.request.UpdateMyInfoRequest;
import com.spicy.backend.user.dto.request.UpdatePasswordRequest;
import com.spicy.backend.user.dto.response.MyInfoResponse;
import com.spicy.backend.user.dto.response.UserHQViewResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 회원 정보를 조회한다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getMe() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "특정 회원 조회", description = "HQ 권한을 가진 관리자만 특정 회원의 정보를 조회할 수 있다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserHQViewResponse>> getUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "회원 정보 수정", description = "로그인한 사용자의 회원 정보를 수정한다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMe(
            @Valid @RequestBody UpdateMyInfoRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "비밀번호 변경", description = "기존 비밀번호를 확인한 후 새로운 비밀번호로 변경한다.")
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자가 자신의 계정을 탈퇴 처리한다.")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
