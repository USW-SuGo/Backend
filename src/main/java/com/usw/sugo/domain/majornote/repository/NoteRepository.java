package com.usw.sugo.domain.majornote.repository;

import com.usw.sugo.domain.majornote.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long>, CustomNoteRepository {
}
