package com.ikdaman.domain.auth.service;

import com.ikdaman.global.auth.enumerate.Provider;
import com.ikdaman.global.auth.client.SocialTokenClient;
import com.ikdaman.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.ikdaman.global.exception.ErrorCode.INVALID_SOCIAL_PROVIDER;

/**
 * Provider에 맞는 SocialTokenClient를 찾아 토큰을 검증하고 providerId를 추출
 */
@Service
@RequiredArgsConstructor
public class SocialTokenValidator {

    private final Map<String, SocialTokenClient> clients;

    public String validate(Provider provider, String token) {
        SocialTokenClient client = clients.values().stream()
                .filter(c -> c.provider() == provider)
                .findFirst().orElse(null);
        if (client == null) throw new BaseException(INVALID_SOCIAL_PROVIDER);
        return client.extractProviderId(token);
    }
}
