package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.user.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>, CustomNoteRepository {

    void deleteByCreatingUser(User requestUser);

    void deleteByOpponentUser(User requestUser);

    void deleteById(Long id);

    Optional<Note> findByCreatingUserAndId(User creatingUser, Long id);

    Optional<Note> findByOpponentUserAndId(User opponentUser, Long id);
}
