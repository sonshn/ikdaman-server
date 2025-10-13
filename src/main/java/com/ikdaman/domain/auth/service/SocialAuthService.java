package com.ikdaman.domain.auth.service;

import com.ikdaman.domain.auth.model.AuthReq;
import com.ikdaman.domain.auth.model.AuthRes;
import com.ikdaman.domain.member.entity.Member;
import com.ikdaman.global.auth.enumerate.Provider;
import com.ikdaman.domain.member.repository.MemberRepository;
import com.ikdaman.domain.member.service.MemberService;
import com.ikdaman.global.auth.token.AuthToken;
import com.ikdaman.global.auth.token.AuthTokenProvider;
import com.ikdaman.global.exception.BaseException;
import com.ikdaman.global.util.RandomNickname;
import com.ikdaman.global.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ikdaman.global.exception.ErrorCode.NOT_MATCH_TOKEN_PROVIDER;

/**
 * 통합 소셜 로그인 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SocialAuthService implements SocialService {

    private final SocialTokenValidator tokenValidator;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final RandomNickname randomNickname;
    private final AuthTokenProvider authTokenProvider;
    private final RedisService redisService;

    @Value("${auth.refresh-token-validity}")
    private long refreshExpiry; // RefreshToken 만료일

    @Override
    public AuthRes login(AuthReq req, String socialToken) {

        // 1. Provider 문자열 → Enum 변환
        Provider provider = Provider.from(req.getProvider());

        // 2. 토큰 검증 및 providerId(sub) 추출
        String providerIdFromToken = tokenValidator.validate(provider, socialToken);

        // 3. 요청의 providerId와 토큰에서 추출한 providerId 일치 검증
        if (!req.getProviderId().equals(providerIdFromToken)) throw new BaseException(NOT_MATCH_TOKEN_PROVIDER);

        // 4. 회원 조회(없으면 생성)
        Member member = memberRepository
                .findBySocialTypeAndProviderId(Member.SocialType.valueOf(provider.name()), providerIdFromToken)
                .orElseGet(() -> {
                    String nickname;
                    do {
                        nickname = randomNickname.generate();
                    } while (!memberService.isAvailableNickname(nickname)); // 닉네임 중복되면 다시 생성

                    // 신규 회원 저장
                    Member newMember = Member.builder()
                            .socialType(Member.SocialType.valueOf(provider.name()))
                            .providerId(providerIdFromToken)
                            .nickname(nickname)
                            .build();
                    return memberRepository.save(newMember);
                });

        // 5. 토큰 발급 및 리프레시 토큰 Redis 저장
        String key = String.valueOf(member.getMemberId());
        AuthToken accessToken = authTokenProvider.createUserAppToken(key);
        AuthToken refreshToken = authTokenProvider.createRefreshToken(key);
        redisService.setValuesWithTimeout(key, refreshToken.getToken(), refreshExpiry);

        return AuthRes.builder()
                .accessToekn(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .nickname(member.getNickname())
                .build();
    }
}
