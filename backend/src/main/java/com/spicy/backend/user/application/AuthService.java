package com.spicy.backend.user.application;

import com.spicy.backend.user.dto.request.LoginRequest;
import com.spicy.backend.user.dto.request.LogoutRequest;
import com.spicy.backend.user.dto.request.SignUpRequest;
import com.spicy.backend.user.dto.response.LoginResponse;

public interface AuthService {

    void signup(SignUpRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse reissue(String refreshToken);
    void logout(LogoutRequest request);
}
