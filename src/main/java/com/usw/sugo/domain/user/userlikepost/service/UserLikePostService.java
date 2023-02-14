package com.usw.sugo.domain.user.userlikepost.service;

import static com.usw.sugo.global.apiresult.ApiResultFactory.getDisLikeFlag;
import static com.usw.sugo.global.apiresult.ApiResultFactory.getLikeFlag;
import static com.usw.sugo.global.exception.ExceptionType.DO_NOT_LIKE_YOURSELF;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLikePostService {

    private final UserLikePostRepository userLikePostRepository;
    private final UserServiceUtility userServiceUtility;
    private final ProductPostService productPostService;
    private final ProductPostFileService productPostFileService;

    public Map<String, Boolean> executeLikeUnlikePost(Long userId, Long productPostId) {
        User user = userServiceUtility.loadUserById(userId);
        ProductPost productPost = productPostService.loadProductPostById(productPostId);
        ProductPostFile productPostFile = productPostFileService.loadProductPostFileByProductPost(
            productPost);
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
            return getLikeFlag();
        }
        userLikePostRepository.deleteLikePostByUserId(userId, productPostId);
        return getDisLikeFlag();
    }


    public boolean isAlreadyLike(Long userId, Long productPostId) {
        return userLikePostRepository.checkUserLikeStatusForPost(userId, productPostId);
    }

    public List<LikePosting> loadLikePosts(Long userId, Pageable pageable) {
        return userLikePostRepository.loadMyLikePosting(userId, pageable);
    }

    @Transactional
    public void deleteByUser(User user) {
        userLikePostRepository.deleteByUser(user);
    }
}
