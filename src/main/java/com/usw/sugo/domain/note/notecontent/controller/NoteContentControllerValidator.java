package com.usw.sugo.domain.note.notecontent.controller;

import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.entityvalidator.EntityValidator;
import org.springframework.stereotype.Component;

@Component
public class NoteContentControllerValidator extends EntityValidator {

    private NoteContentController noteContentController;

    public NoteContentControllerValidator(UserRepository userRepository,
        ProductPostRepository productPostRepository, NoteRepository noteRepository) {
        super(userRepository, productPostRepository, noteRepository);
    }
}
