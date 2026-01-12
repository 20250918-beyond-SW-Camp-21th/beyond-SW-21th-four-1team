package com.spicy.backend.user.application;

import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.user.domain.User;
import com.spicy.backend.user.dto.request.UpdateMyInfoRequest;
import com.spicy.backend.user.dto.request.UpdatePasswordRequest;
import com.spicy.backend.user.dto.response.MyInfoResponse;
import com.spicy.backend.user.error.UserErrorCode;
import com.spicy.backend.user.storage.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    /* 내 정보 조회 테스트 */
    @Test
    void getMe_success() {

        // given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("홍길동")
                .email("test@test.com")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        MyInfoResponse response = userService.getMe(userId);

        // then
        assertThat(response.username()).isEqualTo("홍길동");
        assertThat(response.email()).isEqualTo("test@test.com");
    }

    /* 내 정보 수정 테스트 */
    @Test
    void updateMe_success() {

        // given
        Long userId = 1L;
        User user = User.builder()
                .username("기존이름")
                .email("old@test.com")
                .build();

        UpdateMyInfoRequest request = new UpdateMyInfoRequest("새이름", "new@test.com");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.updateMe(userId, request);

        // then
        assertThat(user.getUsername()).isEqualTo("새이름");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
    }
    /* 비밀번호 변경 테스트 */
    @Test
    void updatePassword_success() {

        // given
        Long userId = 1L;
        User user = User.builder()
                .password("encodedPassword")
                .build();

        UpdatePasswordRequest request = new UpdatePasswordRequest("current", "new");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("current", "encodedPassword")).willReturn(true);
        given(passwordEncoder.encode("new")).willReturn("newEncoded");

        // when
        userService.updatePassword(userId, request);

        // then
        assertThat(user.getPassword()).isEqualTo("newEncoded");
    }

    /* 회원탈퇴 테스트 */
    @Test
    void withdraw_success() {

        // given
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.withdraw(userId);

        // then
        verify(userRepository).delete(user);
    }

    /* 회원 조회 실패 예외 테스트 */
    @Test
    void getMe_userNotFound_throwException() {

        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMe(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }

    /* 비밀번호 불일치 예외 테스트 */
    @Test
    void updatePassword_passwordMismatch_throwException() {

        // given
        Long userId = 1L;
        User user = User.builder()
                .password("encodedPassword")
                .build();

        UpdatePasswordRequest request = new UpdatePasswordRequest("wrong", "newPassword");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.INVALID_PASSWORD);
    }
}
