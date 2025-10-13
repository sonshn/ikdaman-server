package com.ikdaman.global.auth.client;

import com.ikdaman.global.auth.enumerate.Provider;
import com.ikdaman.global.auth.payload.NaverUserRes;
import com.ikdaman.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.ikdaman.global.exception.ErrorCode.INVALID_SOCIAL_ACCESS_TOKEN;
import static com.ikdaman.global.exception.ErrorCode.NAVER_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class NaverClient implements SocialTokenClient {

    private final WebClient webClient;

    public String getUserData(String accessToken) {
        NaverUserRes userRes = webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me") // Naver의 유저 정보 받아오는 url
                .header("Authorization", "Bearer " + accessToken)
//                .headers(h -> h.setBearerAuth("U-" + accessToken)) // 임시로 발급받은 사용자 토큰으로 접근
                .retrieve()
                // onStatus <- error handling
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BaseException(INVALID_SOCIAL_ACCESS_TOKEN)))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BaseException(NAVER_SERVER_ERROR)))
                .bodyToMono(NaverUserRes.class) // 유저 정보를 넣을 DTO 클래스
                .block();

        return userRes.getResponse().getId();
    }

    @Override
    public Provider provider() {
        return Provider.NAVER;
    }

    @Override
    public String extractProviderId(String token) {
        return this.getUserData(token);
    }
}
