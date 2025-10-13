package com.ikdaman.global.auth.client;

import com.ikdaman.global.auth.enumerate.Provider;
import com.ikdaman.global.auth.payload.OAuthUserRes;
import com.ikdaman.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.ikdaman.global.exception.ErrorCode.INVALID_SOCIAL_ACCESS_TOKEN;
import static com.ikdaman.global.exception.ErrorCode.KAKAO_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class KakaoClient implements SocialTokenClient {

    private final WebClient webClient;

    public String getUserData(String accessToken) {
        OAuthUserRes OAuthUserRes = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me") // Kakao의 유저 정보 받아오는 url
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .headers(h -> h.setBearerAuth("U-" + accessToken)) // 임시로 발급받은 사용자 토큰으로 접근
                .retrieve()
                // onStatus <- error handling
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BaseException(INVALID_SOCIAL_ACCESS_TOKEN)))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BaseException(KAKAO_SERVER_ERROR)))
                .bodyToMono(OAuthUserRes.class) // Kakao의 유저 정보를 넣을 DTO 클래스
                .block();

        return String.valueOf(OAuthUserRes.getId());
    }

    @Override
    public Provider provider() {
        return Provider.KAKAO;
    }

    @Override
    public String extractProviderId(String token) {
        return this.getUserData(token);
    }
}
