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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    /* 회원가입 테스트 */
    @Test
    void signup_success() {

        // given
        SignUpRequest request = new SignUpRequest("testId", "password", "홍길동", "test@test.com");

        given(userRepository.existsByLoginId("testId")).willReturn(false);
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");

        // when
        authService.signup(request);

        // then
        verify(userRepository).save(any(User.class));
    }

    /* 로그인 테스트 */
    @Test
    void login_success() {

        // given
        LoginRequest request = new LoginRequest("testId", "password");

        User user = User.builder()
                .id(1L)
                .loginId("testId")
                .password("encodedPassword")
                .userRole(UserRole.FRANCHISE)
                .build();

        given(userRepository.findByLoginId("testId")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true);
        given(jwtProvider.createAccessToken(user)).willReturn("accessToken");
        given(jwtProvider.createRefreshToken(user)).willReturn("refreshToken");
        given(jwtProvider.getRefreshTokenExpiredAt()).willReturn(LocalDateTime.now().plusDays(7));

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");

        verify(refreshTokenRepository).deleteByUserId(1L);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    /* 토큰 재발급 테스트 */
    @Test
    void reissue_success() {

        // given
        String oldRefreshToken = "oldToken";
        RefreshToken savedToken = new RefreshToken(1L, oldRefreshToken, LocalDateTime.now().plusDays(7));
        User user = User.builder().id(1L).build();

        given(jwtProvider.validateToken(oldRefreshToken)).willReturn(true);
        given(refreshTokenRepository.findByToken(oldRefreshToken)).willReturn(Optional.of(savedToken));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(jwtProvider.createAccessToken(user)).willReturn("newAccess");
        given(jwtProvider.createRefreshToken(user)).willReturn("newRefresh");
        given(jwtProvider.getRefreshTokenExpiredAt()).willReturn(LocalDateTime.now().plusDays(7));

        // when
        LoginResponse response = authService.reissue(oldRefreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccess");
        assertThat(response.refreshToken()).isEqualTo("newRefresh");

        verify(refreshTokenRepository).delete(savedToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    /* 로그아웃 테스트 */
    @Test
    void logout_success() {

        // given
        LogoutRequest request = new LogoutRequest("refreshToken");

        // when
        authService.logout(request);

        // then
        verify(refreshTokenRepository).deleteByToken("refreshToken");
    }

    /* 중복 로그인 ID 예외 테스트 */
    @Test
    void signup_duplicateLoginId_throwException() {

        // given
        SignUpRequest request = new SignUpRequest("testId", "password", "홍길동", "test@test.com");
        given(userRepository.existsByLoginId("testId")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.DUPLICATE_LOGIN_ID);
    }

    /* 로그인 시 비밀번호 불일치 예외 테스트 */
    @Test
    void login_invalidPassword_throwException() {
        // given
        LoginRequest request = new LoginRequest("testId", "wrong");
        User user = User.builder().password("encodedPassword").build();

        given(userRepository.findByLoginId("testId")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.INVALID_LOGIN);
    }

    /* 토큰 재발급 시 유효하지 않은 토큰 예외 테스트 */
    @Test
    void reissue_invalidToken_throwException() {

        // given
        String invalidToken = "invalidToken";
        given(jwtProvider.validateToken(invalidToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue(invalidToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.INVALID_TOKEN);
    }
}
