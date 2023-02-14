package com.usw.sugo.domain.notice.repository;

import com.usw.sugo.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository {

}
