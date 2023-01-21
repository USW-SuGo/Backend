package com.usw.sugo.domain.notecontent.controller;

import com.usw.sugo.domain.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.repository.UserRepository;
import com.usw.sugo.global.entityvalidator.EntityValidator;
import org.springframework.stereotype.Component;

@Component
public class NoteContentControllerValidator extends EntityValidator {

    private NoteContentController noteContentController;

    public NoteContentControllerValidator(UserRepository userRepository, ProductPostRepository productPostRepository, NoteRepository noteRepository) {
        super(userRepository, productPostRepository, noteRepository);
    }
}
