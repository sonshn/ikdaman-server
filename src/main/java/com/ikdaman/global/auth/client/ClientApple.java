package com.ikdaman.global.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikdaman.global.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.ikdaman.global.exception.ErrorCode.APPLE_SERVER_ERROR;
import static com.ikdaman.global.exception.ErrorCode.INVALID_SOCIAL_ACCESS_TOKEN;

@Component
@RequiredArgsConstructor
public class ClientApple {

    @Value("${auth.apple.client-id}")
    private String clientId;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * Apple ID 토큰을 검증하고 사용자 정보를 추출
     *
     * @param idToken Apple ID 토큰
     * @return 사용자 Apple 계정의 providerId
     */
    public String getUserDataByIdToken(String idToken) {
        try {
            // Apple의 공개키 목록 조회
            AppleKeysResponse keysResponse = webClient.get()
                    .uri("https://appleid.apple.com/auth/keys")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response
                            -> Mono.error(new BaseException(INVALID_SOCIAL_ACCESS_TOKEN)))
                    .onStatus(status -> status.is5xxServerError(), response
                            -> Mono.error(new BaseException(APPLE_SERVER_ERROR)))
                    .bodyToMono(AppleKeysResponse.class)
                    .block();

            if (keysResponse == null || keysResponse.getKeys() == null) {
                throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);
            }

            // ID 토큰을 파싱하여 헤더 정보 추출
            String[] tokenParts = idToken.split("\\.");
            if (tokenParts.length != 3) {
                throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);
            }

            // 헤더 디코딩
            String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
            Map<String, Object> header;
            try {
                header = objectMapper.readValue(headerJson, Map.class);
            } catch (Exception e) {
                throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);
            }
            String kid = (String) header.get("kid");
            String alg = (String) header.get("alg");

            // 해당 키 찾기
            AppleKey matchingKey = keysResponse.getKeys().stream()
                    .filter(key -> key.getKid().equals(kid))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(INVALID_SOCIAL_ACCESS_TOKEN));

            // 공개키 생성
            PublicKey publicKey = createPublicKey(matchingKey);

            // 토큰 검증 및 페이로드 추출
            Claims claims = verifyAndDecodeToken(idToken, publicKey, alg);

            // aud (audience) 검증
            String aud = claims.getAudience();
            if (!clientId.equals(aud)) {
                throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);
            }

            // sub (subject) 반환 - Apple의 providerId
            return claims.getSubject();

        } catch (Exception e) {
            if (e instanceof BaseException) {
                throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);
            }
            throw new BaseException(INVALID_SOCIAL_ACCESS_TOKEN);
        }
    }

    /**
     * Apple 공개키를 생성
     */
    private PublicKey createPublicKey(AppleKey key) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    /**
     * 토큰을 검증하고 페이로드를 디코딩
     */
    private Claims verifyAndDecodeToken(String token, PublicKey publicKey, String alg) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Apple Keys Response DTO
    public static class AppleKeysResponse {
        private List<AppleKey> keys;

        public List<AppleKey> getKeys() {
            return keys;
        }

        public void setKeys(List<AppleKey> keys) {
            this.keys = keys;
        }
    }

    // Apple Key DTO
    @Setter
    @Getter
    public static class AppleKey {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }
}
