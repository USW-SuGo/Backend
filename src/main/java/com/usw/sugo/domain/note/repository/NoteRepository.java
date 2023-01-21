package com.usw.sugo.domain.note.repository;

import com.usw.sugo.domain.note.Note;
import com.usw.sugo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NoteRepository extends JpaRepository<Note, Long>, CustomNoteRepository {
    void deleteByCreatingUser(User requestUser);

    void deleteByOpponentUser(User requestUser);

    List<Note> findByCreatingUser(User requestUser);

    List<Note> findByOpponentUser(User requestUser);
}
