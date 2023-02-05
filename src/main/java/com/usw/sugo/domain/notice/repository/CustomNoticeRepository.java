package com.usw.sugo.domain.notice.repository;

import com.usw.sugo.domain.notice.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomNoticeRepository{

    List<Notice> loadAllNotice(Pageable pageable);
}
