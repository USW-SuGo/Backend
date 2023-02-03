package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.user.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>, CustomNoteRepository {
    void deleteByCreatingUser(User requestUser);
    void deleteByOpponentUser(User requestUser);
    void deleteById(Long Id);

    List<Note> findByCreatingUser(User requestUser);

    List<Note> findByOpponentUser(User requestUser);
}
