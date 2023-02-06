package com.usw.sugo.domain.user.userlikepost.repository;


import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.LikePosting;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomUserLikePostRepository {

    boolean checkUserLikeStatusForPost(Long userId, Long productPostId);

    void deleteLikePostByUserId(Long userId, Long productPostId);

    List<LikePosting> loadMyLikePosting(Long userId, Pageable pageable);
}
