package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>, CustomNoteRepository {

    void deleteByCreatingUser(User requestUser);

    void deleteByOpponentUser(User requestUser);

    void deleteById(Long id);

    Optional<Note> findByCreatingUserAndId(User creatingUser, Long id);

    Optional<Note> findByOpponentUserAndId(User opponentUser, Long id);

    List<Note> findByProductPost(ProductPost productPost);

    @Query("SELECT n FROM Note n WHERE n.creatingUser = :user OR n.opponentUser = :user")
    List<Note> findByUser(@Param("user") User user);
}
