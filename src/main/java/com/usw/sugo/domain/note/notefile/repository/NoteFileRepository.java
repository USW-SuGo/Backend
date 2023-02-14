package com.usw.sugo.domain.note.notefile.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.notefile.NoteFile;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteFileRepository extends JpaRepository<NoteFile, Long>,
    CustomNoteFileRepository {

    List<NoteFile> findBySender(User requestUser);

    List<NoteFile> findByReceiver(User requestUser);

    Optional<NoteFile> findByNote(Note note);

    void deleteByNote(Note note);
}
