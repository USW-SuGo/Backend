package com.usw.sugo.domain.note.notecontent.repository;

import com.usw.sugo.domain.note.NoteContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteContentRepository extends JpaRepository<NoteContent, Long> {
}
