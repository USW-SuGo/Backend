package com.usw.sugo.domain.note.notefile.repository;

import com.usw.sugo.domain.note.entity.NoteFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteFileRepository extends JpaRepository<NoteFile, Long> {
}
