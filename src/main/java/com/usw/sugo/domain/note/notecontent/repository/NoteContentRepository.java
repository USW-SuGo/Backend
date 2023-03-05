package com.usw.sugo.domain.note.notecontent.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface NoteContentRepository extends JpaRepository<NoteContent, Long>,
    CustomNoteContentRepository {

    List<NoteContent> findByNote(Note note);

    @Query("SELECT nc FROM NoteContent nc WHERE nc.sender = :user OR nc.receiver = :user")
    List<NoteContent> findByUser(@Param("user") User user);
}
