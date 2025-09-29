package com.ikdaman.domain.notice.service;

import com.ikdaman.domain.notice.entity.Notice;
import com.ikdaman.domain.notice.model.NoticeListRes;
import com.ikdaman.domain.notice.model.NoticeReq;
import com.ikdaman.domain.notice.model.NoticeRes;
import com.ikdaman.domain.notice.repository.NoticeRepository;
import com.ikdaman.global.exception.BaseException;
import com.ikdaman.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public NoticeRes addNotice(NoticeReq request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .noticeWriter(
                        request.getNoticeWriter() == null || request.getNoticeWriter().isBlank()
                                ? "관리자"
                                : request.getNoticeWriter())
                .content(request.getContent())
                .uploadedAt(LocalDateTime.now())
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        return NoticeRes.builder()
                .noticeId(savedNotice.getNoticeId())
                .title(savedNotice.getTitle())
                .content(savedNotice.getContent())
                .uploadedAt(savedNotice.getUploadedAt())
                .noticeWriter(savedNotice.getNoticeWriter())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeListRes getNotices(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit,  Sort.by(Sort.Direction.DESC, "uploadedAt"));

        Page<Notice> noticePage = noticeRepository.findAll(pageable);

        List<NoticeListRes.NoticeListDTO> noticeDTOs = noticePage.stream()
                .map(notice -> new NoticeListRes.NoticeListDTO(
                        notice.getNoticeId(),
                        notice.getTitle(),
                        notice.getUploadedAt()
                ))
                .toList();

        return new NoticeListRes(
                noticeDTOs,
                noticePage.hasNext(),
                noticePage.getNumber() + 1,
                noticePage.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeRes getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_NOTICE));

        return new NoticeRes(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getUploadedAt(),
                notice.getNoticeWriter()
        );
    }
}
