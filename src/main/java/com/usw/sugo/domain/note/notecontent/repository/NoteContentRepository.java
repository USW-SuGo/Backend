package com.usw.sugo.domain.note.notecontent.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface NoteContentRepository extends JpaRepository<NoteContent, Long>,
    CustomNoteContentRepository {

    void deleteBySender(User requestUser);

    void deleteByReceiver(User requestUser);

    void deleteByNote(Note note);

    List<NoteContent> findByNote(Note note);
}
