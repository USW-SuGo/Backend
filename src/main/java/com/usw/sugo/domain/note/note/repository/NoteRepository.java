package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long>, CustomNoteRepository {
}
