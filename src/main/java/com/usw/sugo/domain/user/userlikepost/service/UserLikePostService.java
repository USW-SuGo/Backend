package com.usw.sugo.domain.user.userlikepost.service;

import com.usw.sugo.domain.user.userlikepost.repository.UserLikePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLikePostService {

    private final UserLikePostRepository userLikePostRepository;


    // 이미 좋아요 한 게시물인지 확인하는 메서드
    // 좋아요를 했다면 true
    public boolean isAlreadyLike(long userId, long productPostId) {
        return userLikePostRepository.checkUserLikeStatusForPost(userId, productPostId);
    }
}
