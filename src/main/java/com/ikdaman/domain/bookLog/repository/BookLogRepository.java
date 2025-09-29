package com.ikdaman.domain.bookLog.repository;

import com.ikdaman.domain.bookLog.entity.BookLog;
import com.ikdaman.domain.mybook.entity.MyBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookLogRepository extends JpaRepository<BookLog, Long> {
    Optional<BookLog> findFirstByMyBookAndBooklogType(MyBook myBook, String booklogType);
    Page<BookLog> findByMyBook_MybookId(Long mybookId, Pageable pageable);
    void deleteByMyBook(MyBook myBook);
}
