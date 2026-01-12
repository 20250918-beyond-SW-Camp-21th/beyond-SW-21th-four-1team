package com.spicy.backend.user.application;

import com.spicy.backend.user.dto.request.UpdateMyInfoRequest;
import com.spicy.backend.user.dto.request.UpdatePasswordRequest;
import com.spicy.backend.user.dto.response.MyInfoResponse;
import com.spicy.backend.user.dto.response.UserHQViewResponse;

public interface UserService {

    MyInfoResponse getMe(Long userId);
    UserHQViewResponse getUser(Long userId);
    void updateMe(Long userId, UpdateMyInfoRequest request);
    void updatePassword(Long userId, UpdatePasswordRequest request);
    void withdraw(Long userId);
}
