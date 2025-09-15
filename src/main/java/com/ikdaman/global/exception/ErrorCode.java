package com.ikdaman.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /**
     * 400 BAD_REQUEST
     */
    // common(00)
    BAD_REQUEST_BY_VALIDATION(HttpStatus.BAD_REQUEST.value(), 4000001, "유효하지 않은 값입니다."),

    /**
     * 401 Unauthorized
     */
    // Auth(01)

    // Member(02)

    // MyBook(03)

    // Notice(04)


    /**
     * 403 Forbidden
     */
    // Auth(01)

    // Member(02)

    // MyBook(03)
    BOOK_NOT_OWNED_BY_MEMBER(HttpStatus.FORBIDDEN.value(), 4030301, "본인의 책이 아닙니다."),

    // Notice(04)


    /**
     * 404 Not found
     */
    // Auth(01)
    INVALID_SOCIAL_ACCESS_TOKEN(HttpStatus.NOT_FOUND.value(), 4040101,"Social Access Token이 유효하지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.NOT_FOUND.value(), 4040102,"Access Token이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.NOT_FOUND.value(), 4040103,"Refresh Token이 유효하지 않습니다."),
    INVALID_SOCIAL_PROVIDER(HttpStatus.NOT_FOUND.value(), 4040104,"유효하지 않은 소셜 로그인 제공업체입니다."),
    NOT_MATCH_TOKEN_PROVIDER(HttpStatus.NOT_FOUND.value(), 4040105,"Social Access Token과 Provider Id가 매칭되지 않습니다."),
    INVALID_ACCESS_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), 4010106, "Access Token의 서명이 유효하지 않습니다."),
    INVALID_ACCESS_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED.value(), 4010107, "Access Token의 형식이 유효하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), 4010108, "Access Token이 만료되었습니다."),
    UNSUPPORTED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), 4010109, "Access Token이 지원되지 않습니다."),

    // Member(02)
    NOT_FOUND_USER(HttpStatus.NOT_FOUND.value(), 4040201, "존재하지 않는 유저입니다."),

    // MyBook(03)
    NOT_FOUND_BOOK(HttpStatus.NOT_FOUND.value(), 4040301, "해당하는 책의 정보가 존재하지 않습니다."),
    NOT_FOUND_AUTHOR(HttpStatus.NOT_FOUND.value(), 4040302, "해당하는 작가의 정보가 존재하지 않습니다."),
    EMPTY_IMPRESSION(HttpStatus.BAD_REQUEST.value(), 4040303, "첫인상을 입력해주세요."),

    // Notice(04)
    NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND.value(), 4040301, "해당하는 공지사항이 존재하지 않습니다."),

    /**
     * 409 Conflict
     */
    // Auth(01)

    // Member(02)
    CONFLICT_NICKNAME(HttpStatus.CONFLICT.value(), 4090201, "사용중인 닉네임입니다."),
    // MyBook(03)
    NOT_FOUND_MY_BOOK(HttpStatus.NOT_FOUND.value(), 4040301,"해당 MyBook을 찾을 수 없습니다."),
    MY_BOOK_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), 4090302, "이미 가지고 있는 책이에요!"),

    // Notice(04)

    /**
     * 500 Internal Server Error
     */
    // Common(00)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000001, "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000002,"데이터베이스 오류가 발생했습니다."),
    NULL_POINTER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000003,"잘못된 값(NULL)이 처리되었습니다."),
    PERSIST_EXCEPTION_TO_THIRD_PARTY(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000004,"외부 저장소에 저장을 실패했습니다."),

    // Auth(01)
    FAILED_GENERATE_APP_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000101, "ikdaman의 앱 토큰 생성에 실패했습니다."),
    KAKAO_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000102, "카카오 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    GOOGLE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000103, "구글 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    NAVER_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000104, "네이버 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    APPLE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5000105, "애플 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
    ;

    private final int status;
    private final int code;
    private final String message;

}
