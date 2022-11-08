package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.Note;
import com.usw.sugo.domain.note.note.repository.CustomNoteRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long>, CustomNoteRepository {
}
