package com.spicy.backend.global.jwt;

import com.spicy.backend.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public JwtProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long accessTokenExpireTime,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpireTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("role", user.getUserRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpireTime))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpireTime))
                .signWith(secretKey)
                .compact();
    }

    public LocalDateTime getRefreshTokenExpiredAt() {
        return LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpireTime));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("JWT 서명 검증에 실패했습니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다.");
        }
        return false;
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }
}
