package com.spicy.backend.user.domain;

import com.spicy.backend.global.entity.BaseEntity;
import com.spicy.backend.user.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인 아이디
    @Column(nullable = false, unique = true)
    private String loginId;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    // 회원 이름
    @Column(nullable = false)
    private String username;

    // 회원 이메일
    @Column(nullable = false)
    private String email;

    // 회원 종류 - HQ(본사 관리자), FRANCHISE(가맹점 관리자)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public void updateInfo(String name, String email) {
        this.username = name;
        this.email = email;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
