package com.spicy.backend.user.domain;

import com.spicy.backend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 아이디
    @Column(nullable = false)
    private Long userId;

    // AccessToken 재발급에 사용되는 RefreshToken 값
    @Column(nullable = false, unique = true)
    private String token;

    // RefreshToken 유효 기간 만료 시점
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public RefreshToken(Long userId, String token, LocalDateTime expiredAt) {
        this.userId = userId;
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
