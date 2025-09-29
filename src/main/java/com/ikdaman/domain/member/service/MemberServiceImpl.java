package com.ikdaman.domain.member.service;

import com.ikdaman.domain.auth.service.AuthService;
import com.ikdaman.domain.member.entity.Member;
import com.ikdaman.domain.member.model.MemberReq;
import com.ikdaman.domain.member.model.MemberRes;
import com.ikdaman.domain.member.repository.MemberRepository;
import com.ikdaman.global.exception.BaseException;
import com.ikdaman.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * 회원 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public Member createMember(MemberReq dto) {

        // 중복된 닉네임 체크
        if (memberRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        Member member = dto.toEntity();
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findMemberById(UUID memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isAvailableNickname(String nickname) {
        return !memberRepository.existsByNicknameAndStatus(nickname, Member.Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberRes getMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        MemberRes info = MemberRes.builder()
                .nickname(member.getNickname())
                .birthdate(member.getBirthdate())
                .gender(member.getGender())
                .build();
        return info;
    }

    @Override
    @Transactional
    public MemberRes editMember(UUID memberId, MemberReq memberReq) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        if(
                memberReq.getNickname() != null
                && !memberReq.getNickname().equals(member.getNickname())
                && !isAvailableNickname(memberReq.getNickname())
        ) {
            throw new BaseException(ErrorCode.CONFLICT_NICKNAME);
        }

        member.updateNickname(Optional.ofNullable(memberReq.getNickname()).orElse(member.getNickname()));
        // birthdate 필드를 null인 채로 보냈다면 갱신하지 않음, 빈 문자열로 보낸다면 null 처리 (빈 문자열은 Deserializer에서 LocalDate.MIN 처리 됨)
        LocalDate birthDate = Optional.ofNullable(memberReq.getBirthdate()).orElse(member.getBirthdate());
        member.updateBirthdate((birthDate != LocalDate.MIN) ? birthDate : null);
        // gender 필드를 null인 채로 보냈다면 갱신 하지 않음, 빈 문자열로 보낸다면 null 처리
        Member.Gender gender = Optional.ofNullable(memberReq.getGender()).orElse(member.getGender());
        member.updateGender((gender != Member.Gender.BLANK) ? gender : null);

        memberRepository.save(member);

        MemberRes info = MemberRes.builder()
                .nickname(member.getNickname())
                .gender(member.getGender())
                .birthdate(member.getBirthdate())
                .build();
        return info;
    }

    @Override
    @Transactional
    public void withdrawMember(Member member) {
        authService.logout(member.getMemberId());

        member.updateStatus(Member.Status.INACTIVE);
        member.updateProviderId(null);
        memberRepository.save(member);
    }
}
