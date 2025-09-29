package com.ikdaman.domain.member.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ikdaman.domain.member.deserializer.BirthDateDeserializer;
import com.ikdaman.domain.member.deserializer.GenderDeserializer;
import com.ikdaman.domain.member.entity.Member;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 회원 요청 DTO
 */
// TODO: Validation 확인
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberReq {

    @Size(min=2, max=15, message = "닉네임은 최소 2자, 최대 15자만 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s]+$", message = "닉네임은 한글, 영어, 숫자, 공백만을 포함합니다.")
    private String nickname;
    @JsonDeserialize(using = GenderDeserializer.class)
    private Member.Gender gender;
    @JsonDeserialize(using = BirthDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @Builder
    public MemberReq(String nickname, Member.Gender gender, LocalDate birthdate) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .build();
    }
}
