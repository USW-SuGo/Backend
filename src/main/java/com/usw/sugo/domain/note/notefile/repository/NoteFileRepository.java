package com.usw.sugo.domain.note.notefile.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.notefile.NoteFile;
import com.usw.sugo.domain.user.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteFileRepository extends JpaRepository<NoteFile, Long>, CustomNoteFileRepository {

    List<NoteFile> findBySender(User requestUser);

    List<NoteFile> findByReceiver(User requestUser);

    Optional<NoteFile> findByNote(Note note);

    void deleteByNote(Note note);
}
