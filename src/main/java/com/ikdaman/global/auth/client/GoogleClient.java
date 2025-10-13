package com.ikdaman.global.auth.client;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ikdaman.global.auth.enumerate.Provider;
import com.ikdaman.global.auth.payload.OAuthUserRes;
import com.ikdaman.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static com.ikdaman.global.exception.ErrorCode.GOOGLE_SERVER_ERROR;
import static com.ikdaman.global.exception.ErrorCode.INVALID_SOCIAL_ACCESS_TOKEN;

@Component
@RequiredArgsConstructor
public class GoogleClient implements SocialTokenClient {

    @Value("${auth.google.client-id.android}")
    private String androidClientId;

    @Value("${auth.google.client-id.ios}")
    private String iosClientId;

    @Value("${auth.google.client-id.web}")
    private String webClientId;

    private final WebClient webClient;

    /**
     * accessToken을 사용하여 Google의 userinfo API에서 사용자 정보 조회
     *
     * @param accessToken Google accessToken
     * @return 사용자 Google 계정의 providerId
     */
    public String getUserDataByAccessToken(String accessToken) {

        // WebClient를 사용해 Google API 호출
        OAuthUserRes OAuthUserRes = webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo") // Google 사용자 정보 요청 URL
                .header("Authorization", "Bearer " + accessToken)
//                .headers(h -> h.setBearerAuth("U-" + accessToken)) // 임시로 발급받은 사용자 토큰으로 접근
                .retrieve()
                // 4xx 에러 처리
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BaseException(INVALID_SOCIAL_ACCESS_TOKEN)))
                // 5xx 에러 처리
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BaseException(GOOGLE_SERVER_ERROR)))
                .bodyToMono(OAuthUserRes.class) // 유저 정보를 넣을 DTO 클래스
                .block();

        return String.valueOf(OAuthUserRes.getId());
    }

    /**
     * idToken을 검증 및 파싱하여 Google의 사용자 정보 추출
     *
     * @param idToken Google idToken
     * @return 사용자 Google 계정의 providerId
     */
    public String getUserDataByIdToken(String idToken) {
        try {
            // GoogleIdTokenVerifier를 생성: Google의 공개키로 idToken의 서명을 검증
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    // idToken의 Audience 설정: 웹, Android, iOS client ID를 모두 허용
                    .setAudience(Arrays.asList(
                            webClientId,
                            androidClientId,
                            iosClientId
                    ))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);
            }

            // Payload에서 Google의 "sub" (providerId) 추출
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            return payload.getSubject();
        } catch (GeneralSecurityException e) {
            throw new BaseException(GOOGLE_SERVER_ERROR);
        } catch (IOException e) {
            throw new BaseException(GOOGLE_SERVER_ERROR);
        }
    }

    @Override
    public Provider provider() {
        return Provider.GOOGLE;
    }

    @Override
    public String extractProviderId(String token) {
        return this.getUserDataByIdToken(token);
    }
}
