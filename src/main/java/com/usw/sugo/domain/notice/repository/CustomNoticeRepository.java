package com.usw.sugo.domain.notice.repository;

import com.usw.sugo.domain.notice.Notice;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomNoticeRepository {

    List<Notice> loadAllNotice(Pageable pageable);
}
