package com.usw.sugo.domain.userlikepostnote;

import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.userlikepost.repository.UserLikePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLikePostAndNoteService {

    private final UserLikePostRepository userLikePostRepository;
    private final NoteRepository noteRepository;

    public Integer loadLikeCountByProductPost(ProductPost productPost) {
        return userLikePostRepository.findByProductPost(productPost).size();
    }

    public Integer loadNoteCountByProductPost(ProductPost productPost) {
        return noteRepository.findByProductPost(productPost).size();
    }
}
