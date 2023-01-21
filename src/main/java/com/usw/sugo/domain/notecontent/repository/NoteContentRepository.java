package com.usw.sugo.domain.notecontent.repository;

import com.usw.sugo.domain.notecontent.NoteContent;
import com.usw.sugo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface NoteContentRepository extends JpaRepository<NoteContent, Long>, CustomNoteContentRepository {
    void deleteBySender(User requestUser);
    void deleteByReceiver(User requestUser);
}
