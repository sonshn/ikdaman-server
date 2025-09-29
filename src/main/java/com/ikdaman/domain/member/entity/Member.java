package com.ikdaman.domain.member.entity;

import com.ikdaman.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 회원 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicInsert
@DynamicUpdate
public class Member extends BaseTime {
    @Id
    @GeneratedValue
    @Column(name = "member_id", nullable = false, updatable = false, columnDefinition = "UUID DEFAULT uuid_generate_v7()")
    private UUID memberId;

    // TODO: NOT NULL, RANDOM GENERATE
    @Column(length = 20)
    @Size(min=1, max=20)
    private String nickname;

    @Column
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "provider_id", unique = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE; // 기본값 ACTIVE

    // 성별 ENUM
    public enum Gender {
        FEMALE, MALE, BLANK
    }

    // 소셜 로그인 제공자 ENUM
    public enum SocialType {
        KAKAO, NAVER, GOOGLE, APPLE
    }

    // 계정 상태 ENUM
    public enum Status {
        ACTIVE, INACTIVE
    }

    @Builder
    public Member(UUID memberId, String nickname, LocalDate birthdate, Gender gender, SocialType socialType, String providerId, Status status) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.socialType = socialType;
        this.providerId = providerId;
        this.status = status;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateBirthdate(LocalDate newBirthDate) {
        this.birthdate = newBirthDate;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateProviderId(String providerId) {
        this.providerId = providerId;
    }
}
