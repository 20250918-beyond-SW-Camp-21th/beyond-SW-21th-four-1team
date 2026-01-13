package com.spicy.backend.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 삭제 일시
    private LocalDateTime deletedAt;

    // 삭제 처리 메소드
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 삭제 확인 메소드
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
