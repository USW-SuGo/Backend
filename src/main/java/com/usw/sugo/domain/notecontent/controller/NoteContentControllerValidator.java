package com.usw.sugo.domain.notecontent.controller;

import com.usw.sugo.domain.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.repository.UserRepository;
import com.usw.sugo.global.entityvalidator.EntityValidator;
import com.usw.sugo.global.exception.CustomException;
import org.springframework.stereotype.Component;

import static com.usw.sugo.global.exception.ExceptionType.DO_NOT_CREATE_YOURSELF;
import static com.usw.sugo.global.exception.ExceptionType.NOTE_ALREADY_CREATED;

@Component
public class NoteContentControllerValidator extends EntityValidator {
    private NoteRepository noteRepository;

    public NoteContentControllerValidator(UserRepository userRepository, ProductPostRepository productPostRepository, NoteRepository noteRepository) {
        super(userRepository, productPostRepository, noteRepository);
    }

    public void validateCreatingNoteRoom(long creatingRequestUserId, long opponentUserId, long productPostId) {

        if (creatingRequestUserId == opponentUserId) {
            throw new CustomException(DO_NOT_CREATE_YOURSELF);
        }

        noteRepository.findNoteByRequestUserAndTargetUserAndProductPost(
                        creatingRequestUserId, opponentUserId, productPostId)
                .ifPresent(t -> {
                    throw new CustomException(NOTE_ALREADY_CREATED);
                });
    }
}
