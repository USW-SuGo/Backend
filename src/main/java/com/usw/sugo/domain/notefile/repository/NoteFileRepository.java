package com.usw.sugo.domain.notefile.repository;

import com.usw.sugo.domain.notefile.NoteFile;
import com.usw.sugo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteFileRepository extends JpaRepository<NoteFile, Long>, CustomNoteFileRepository {

    List<NoteFile> findBySender(User requestUser);

    List<NoteFile> findByReceiver(User requestUser);
}
