package com.usw.sugo.domain.user.userlikepost.service;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.LikePosting;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import com.usw.sugo.domain.productpost.productpostfile.service.ProductPostFileService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.domain.user.userlikepost.UserLikePost;
import com.usw.sugo.domain.user.userlikepost.repository.UserLikePostRepository;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.usw.sugo.domain.ApiResult.LIKE;
import static com.usw.sugo.global.exception.ExceptionType.DO_NOT_LIKE_YOURSELF;

@Service
@RequiredArgsConstructor
public class UserLikePostService {

    private final UserLikePostRepository userLikePostRepository;
    private final UserServiceUtility userServiceUtility;
    private final ProductPostService productPostService;
    private final ProductPostFileService productPostFileService;

    private static final Map<String, Boolean> likeCreated = new HashMap<>() {{
        put(LIKE.getResult(), true);
    }};

    private static final Map<String, Boolean> likeDeleted = new HashMap<>() {{
        put(LIKE.getResult(), true);
    }};

    public Map<String, Boolean> executeLikeUnlikePost(Long userId, Long productPostId) {
        User user = userServiceUtility.loadUserById(userId);
        ProductPost productPost = productPostService.loadProductPostById(productPostId);
        ProductPostFile productPostFile = productPostFileService.loadProductPostFileByProductPost(productPost);
        if (productPost.getUser().equals(user)) {
            throw new CustomException(DO_NOT_LIKE_YOURSELF);
        } else if (!isAlreadyLike(userId, productPostId)) {
            UserLikePost userLikePost = UserLikePost.builder()
                    .user(user)
                    .productPost(productPost)
                    .productPostFile(productPostFile)
                    .createdAt(LocalDateTime.now())
                    .build();
            userLikePostRepository.save(userLikePost);
            return likeCreated;
        }
        userLikePostRepository.deleteLikePostByUserId(userId, productPostId);
        return likeDeleted;
    }


    public boolean isAlreadyLike(Long userId, Long productPostId) {
        return userLikePostRepository.checkUserLikeStatusForPost(userId, productPostId);
    }

    public List<LikePosting> loadLikePosts(Long userId, Pageable pageable) {
        return userLikePostRepository.loadMyLikePosting(userId, pageable);
    }
}
