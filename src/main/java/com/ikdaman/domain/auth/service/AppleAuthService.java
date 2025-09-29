package com.ikdaman.domain.auth.service;

import com.ikdaman.domain.auth.model.AuthReq;
import com.ikdaman.domain.auth.model.AuthRes;
import com.ikdaman.domain.member.entity.Member;
import com.ikdaman.domain.member.repository.MemberRepository;
import com.ikdaman.domain.member.service.MemberService;
import com.ikdaman.global.auth.client.ClientApple;
import com.ikdaman.global.auth.token.AuthToken;
import com.ikdaman.global.auth.token.AuthTokenProvider;
import com.ikdaman.global.exception.BaseException;
import com.ikdaman.global.util.RandomNickname;
import com.ikdaman.global.util.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.ikdaman.global.exception.ErrorCode.APPLE_SERVER_ERROR;
import static com.ikdaman.global.exception.ErrorCode.INVALID_SOCIAL_ACCESS_TOKEN;

@Service("apple")
@RequiredArgsConstructor
public class AppleAuthService implements OAuthService {

    private final ClientApple clientApple;
    private final AuthTokenProvider authTokenProvider;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final RandomNickname randomNickname;

    @Value("${auth.refresh-token-validity}")
    private long refreshExpiry;

    @Override
    @Transactional
    public AuthRes login(AuthReq dto, String socialToken) {

        // 1. 소셜 idToken을 통해 Apple userId 가져오기
        final String checkProviderId;
        try {
            checkProviderId = clientApple.getUserDataByIdToken(socialToken);
        } catch (Exception e) {
            throw new BaseException(APPLE_SERVER_ERROR);
        }

        // 2. 요청으로 들어온 providerId와 비교해서 검증
        if (!dto.getProviderId().equals(checkProviderId)) throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);

        // 3. 기존 회원 조회
        Member member = memberRepository.findBySocialTypeAndProviderId(Member.SocialType.APPLE, checkProviderId)
                .orElseGet(() -> {
                    String nickname;
                    do {
                        nickname = randomNickname.generate();
                    } while (!memberService.isAvailableNickname(nickname)); // 닉네임 중복되면 다시 생성

                    // 유저 정보 저장
                    Member newMember = Member.builder()
                            .socialType(Member.SocialType.APPLE)
                            .providerId(checkProviderId)
                            .nickname(nickname)
                            .build();
                    return memberRepository.save(newMember);
                });

        // 4. 신규 토큰 생성 및 저장
        AuthToken accessToken = authTokenProvider.createUserAppToken(String.valueOf(member.getMemberId()));
        AuthToken refreshToken = authTokenProvider.createRefreshToken(String.valueOf(member.getMemberId()));
        redisService.setValuesWithTimeout(String.valueOf(member.getMemberId()), refreshToken.getToken(), refreshExpiry);

        return AuthRes.builder()
                .accessToekn(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .nickname(member.getNickname())
                .build();
    }
}

