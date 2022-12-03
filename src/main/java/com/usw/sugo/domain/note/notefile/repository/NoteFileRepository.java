package com.usw.sugo.domain.note.notefile.repository;

import com.usw.sugo.domain.note.entity.NoteFile;
import com.usw.sugo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteFileRepository extends JpaRepository<NoteFile, Long>, CustomNoteFileRepository {

    List<NoteFile> findBySender(User requestUser);

    List<NoteFile> findByReceiver(User requestUser);
}
