package com.spicy.backend.user.application;

import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.user.domain.User;
import com.spicy.backend.user.dto.request.UpdateMyInfoRequest;
import com.spicy.backend.user.dto.request.UpdatePasswordRequest;
import com.spicy.backend.user.dto.response.MyInfoResponse;
import com.spicy.backend.user.dto.response.UserHQViewResponse;
import com.spicy.backend.user.error.UserErrorCode;
import com.spicy.backend.user.storage.RefreshTokenRepository;
import com.spicy.backend.user.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public MyInfoResponse getMe(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return MyInfoResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserHQViewResponse getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return UserHQViewResponse.from(user);
    }

    @Override
    public void updateMe(Long userId, UpdateMyInfoRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateInfo(request.username(), request.email());
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Override
    public void withdraw(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteByUserId(userId);
        userRepository.delete(user);
    }
}
