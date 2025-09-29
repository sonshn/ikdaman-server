package com.ikdaman.domain.mybook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MyBookSearchRes {
    private List<BookDto> books;
    private int totalPage;
    private int nowPage;

    @Data
    @Builder
    @AllArgsConstructor
    public static class BookDto {
        private int mybookId;
        private String title;
        private String author;
        private String coverImage;
        private Boolean isCompleted;
    }
}
