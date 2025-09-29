package com.ikdaman.domain.mybook.controller;

import com.ikdaman.domain.bookLog.model.BookLogListRes;
import com.ikdaman.domain.mybook.model.*;
import com.ikdaman.domain.mybook.service.MyBookService;
import com.ikdaman.global.auth.model.AuthMember;
import lombok.RequiredArgsConstructor;
import com.ikdaman.global.auth.model.AuthMember;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mybooks")
@RequiredArgsConstructor
public class MyBookController {

    private final MyBookService myBookService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyBookRes> addMyBook(
            @RequestBody MyBookReq dto,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        MyBookRes myBookRes = myBookService.addMyBook(authMember.getMember().getMemberId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{mybook_id}/impression")
    public ResponseEntity<Void> createImpression(@PathVariable("mybook_id") Integer myBookId,
                                                 @RequestBody ImpressionReq dto,
                                                 @AuthenticationPrincipal AuthMember authMember) {

        myBookService.addImpression(authMember.getMember().getMemberId(), myBookId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{mybook_id}")
    public ResponseEntity<Void> deleteMyBook(@PathVariable Integer mybook_id,
                                             @AuthenticationPrincipal AuthMember authMember) {
        myBookService.deleteMyBook(authMember.getMember().getMemberId(), mybook_id);
        return ResponseEntity.status(HttpStatus.RESET_CONTENT).build();
    }

    @GetMapping()
    public MyBookSearchRes searchMyBooks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "9") Integer limit,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        MyBookSearchReq request = new MyBookSearchReq();
        request.setStatus(status);
        request.setKeyword(keyword);
        request.setPage(page);
        request.setLimit(limit);

        // 임시 인증 방식 - 하진 로컬 테스트용
        // Member member = new Member(UUID.fromString("ce03e8b7-df8e-48f4-a8fa-7ed27a3fcc96"), "test", null, null, null, null, null);
        // AuthMember authMember = new AuthMember(member);

        return myBookService.searchMyBooks(request, authMember);
    }

    // 독서중인 책 목록 조회
    @GetMapping("/in-progress")
    public InProgressBooksRes searchInProgressBooks(
        @AuthenticationPrincipal AuthMember authMember
    ) {
        return myBookService.searchInProgressBooks(authMember);
    }

    // 나의 책 정보 조회
    @GetMapping("/{mybookId}")
    public ResponseEntity<MyBookDetailRes> getMyBookDetail(@AuthenticationPrincipal AuthMember authMember,
                                                           @PathVariable Long mybookId) {
        MyBookDetailRes res = myBookService.getMyBookDetail(authMember.getMember().getMemberId(), mybookId);
        return ResponseEntity.ok(res);
    }

    // 나의 책 기록 조회
    @GetMapping("/{mybookId}/booklog")
    public ResponseEntity<BookLogListRes> getMyBookLogs(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable("mybookId") Long mybookId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "9") Integer limit
    ) {
        return ResponseEntity.ok(myBookService.getMyBookLogs(authMember.getMember().getMemberId(), mybookId, page, limit));
    }
}