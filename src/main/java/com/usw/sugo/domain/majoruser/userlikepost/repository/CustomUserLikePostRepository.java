package com.usw.sugo.domain.majoruser.userlikepost.repository;

import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.LikePosting;

import java.util.List;

public interface CustomUserLikePostRepository {

    boolean checkUserLikeStatusForPost(long userId, long productPostId);

    void deleteLikePostByUserId(long userId, long productPostId);

    List<LikePosting> loadMyLikePosting(long userId);
}
