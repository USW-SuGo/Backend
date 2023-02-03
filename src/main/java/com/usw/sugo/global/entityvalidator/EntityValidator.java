package com.usw.sugo.global.entityvalidator;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.usw.sugo.global.exception.ExceptionType.*;

@Component
@RequiredArgsConstructor
public class EntityValidator {

    private final UserRepository userRepository;
    private final ProductPostRepository productPostRepository;
    private final NoteRepository noteRepository;

    public User validateUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }

    public ProductPost validateProductPost(long productPostId) {
        return productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }

    public Note validateNote(long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new CustomException(NOTE_NOT_FOUNDED));
    }
}
