package com.usw.sugo.domain.user.userlikepost.repository;


import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.LikePosting;
import com.usw.sugo.domain.user.userlikepost.UserLikePost;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomUserLikePostRepository {

    boolean checkUserLikeStatusForPost(Long userId, Long productPostId);

    void deleteLikePostByUserId(Long userId, Long productPostId);

    List<LikePosting> loadMyLikePosting(Long userId, Pageable pageable);

    List<UserLikePost> findByProductPost(ProductPost productPost);

}
