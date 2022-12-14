package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.user.entity.User;
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
