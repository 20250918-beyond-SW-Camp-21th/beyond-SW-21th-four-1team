package com.spicy.backend.user.application;

import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.global.jwt.JwtProvider;
import com.spicy.backend.user.domain.RefreshToken;
import com.spicy.backend.user.domain.User;
import com.spicy.backend.user.dto.request.LoginRequest;
import com.spicy.backend.user.dto.request.LogoutRequest;
import com.spicy.backend.user.dto.request.SignUpRequest;
import com.spicy.backend.user.dto.response.LoginResponse;
import com.spicy.backend.user.enums.UserRole;
import com.spicy.backend.user.error.UserErrorCode;
import com.spicy.backend.user.storage.RefreshTokenRepository;
import com.spicy.backend.user.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void signup(SignUpRequest request) {

        if (userRepository.existsByLoginId(request.loginId())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }

        User user = User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .username(request.username())
                .email(request.email())
                .userRole(UserRole.FRANCHISE)
                .build();

        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_LOGIN);
        }

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        refreshTokenRepository.deleteByUserId(user.getId());

        LocalDateTime expiredAt = jwtProvider.getRefreshTokenExpiredAt();
        refreshTokenRepository.save(new RefreshToken(user.getId(),refreshToken, expiredAt));

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse reissue(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(UserErrorCode.INVALID_TOKEN));

        if (savedToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(savedToken);
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(user);
        String newRefreshToken = jwtProvider.createRefreshToken(user);

        refreshTokenRepository.delete(savedToken);
        refreshTokenRepository.save(new RefreshToken(user.getId(), newRefreshToken, jwtProvider.getRefreshTokenExpiredAt()));

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(LogoutRequest request) {
        refreshTokenRepository.deleteByToken(request.refreshToken());
    }
}
