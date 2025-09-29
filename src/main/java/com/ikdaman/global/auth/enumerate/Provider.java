package com.ikdaman.global.auth.enumerate;

import com.ikdaman.global.exception.BaseException;

import static com.ikdaman.global.exception.ErrorCode.INVALID_SOCIAL_PROVIDER;

/**
 * 소셜 로그인 제공자
 */
public enum Provider {
    KAKAO, NAVER, GOOGLE, APPLE;

    /**
     * 문자열 to Enum
     * @param name  제공자 문자열
     * @return  매칭되는 Provider 값
     */
    public static Provider from(String name) {
        if (name == null) throw new BaseException(INVALID_SOCIAL_PROVIDER);
        return Provider.valueOf(name.trim().toUpperCase());
    }
}
