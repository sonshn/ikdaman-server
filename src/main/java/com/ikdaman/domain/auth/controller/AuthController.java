package com.ikdaman.domain.auth.controller;

import com.ikdaman.domain.auth.model.AuthReq;
import com.ikdaman.domain.auth.model.AuthRes;
import com.ikdaman.domain.auth.service.AuthService;
import com.ikdaman.domain.auth.service.SocialAuthService;
import com.ikdaman.global.auth.model.Tokens;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SocialAuthService socialAuthService;
    private final AuthService authService;

    /**
     * 소셜 로그인
     * @param dto
     * @param socialToken access-token: 카카오, 네이버 | id-token: 구글, 애플
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<AuthRes> socialLogin(@RequestBody AuthReq dto,
                                               @RequestHeader("social-token") String socialToken) {

        AuthRes res = socialAuthService.login(dto, socialToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", res.getAccessToekn());
        headers.add("refresh-token", res.getRefreshToken());

        res = AuthRes.builder()
                .nickname(res.getNickname())
                .build();

        return ResponseEntity.ok()
                .headers(headers)
                .body(res);
    }

    /**
     * Access Token 재발급
     * @param accessToken
     * @param refreshToken
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity reissueToken(@RequestHeader("Authorization") String accessToken,
                                       @RequestHeader("refresh-token") String refreshToken) {

        Tokens tokens = authService.reissueToken(accessToken, refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokens.getAccessToken());
        headers.add("refresh-token", tokens.getRefreshToken());

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    /**
     * 로그아웃
     * @param request
     * @return
     */
    @DeleteMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        UUID memberId = (UUID) request.getAttribute("memberId");
        authService.logout(memberId);

        return ResponseEntity.status(HttpStatus.RESET_CONTENT)
                .build();
    }
}
