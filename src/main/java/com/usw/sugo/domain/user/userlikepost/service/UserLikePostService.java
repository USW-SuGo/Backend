package com.usw.sugo.domain.user.userlikepost.service;

import com.usw.sugo.domain.user.user.dto.UserResponseDto.LikePosting;
import com.usw.sugo.domain.user.userlikepost.repository.UserLikePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLikePostService {

    private final UserLikePostRepository userLikePostRepository;

    public boolean isAlreadyLike(Long userId, Long productPostId) {
        return userLikePostRepository.checkUserLikeStatusForPost(userId, productPostId);
    }

    public List<LikePosting> loadLikePosts(Long userId) {
        return userLikePostRepository.loadMyLikePosting(userId);
    }
}
