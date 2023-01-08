package com.usw.sugo.domain.productpost.userlikepost.controller;

import com.usw.sugo.domain.productpost.entity.ProductPost;
import com.usw.sugo.domain.productpost.entity.ProductPostFile;
import com.usw.sugo.domain.productpost.entity.UserLikePost;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpost.productpostfile.repository.ProductPostFileRepository;
import com.usw.sugo.domain.productpost.userlikepost.dto.UserLikePostRequestDto.LikePostRequest;
import com.usw.sugo.domain.productpost.userlikepost.repository.UserLikePostRepository;
import com.usw.sugo.domain.productpost.userlikepost.service.UserLikePostService;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.usw.sugo.global.exception.ExceptionType.DO_NOT_LIKE_YOURSELF;
import static com.usw.sugo.global.exception.ExceptionType.POST_NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class UserLikePostController {

    private final UserRepository userRepository;
    private final ProductPostRepository productPostRepository;

    private final ProductPostFileRepository productPostFileRepository;
    private final UserLikePostService userLikePostService;
    private final UserLikePostRepository userLikePostRepository;

    private final JwtResolver jwtResolver;

    /**
     * 게시글 좋아요 하기
     *
     * @param authorization
     * @param likePostRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<Map<String, Boolean>> likePost(
            @RequestHeader String authorization,
            @RequestBody LikePostRequest likePostRequest) {

        User requestUser = userRepository.findById(
                jwtResolver.jwtResolveToUserId(authorization.substring(7))).get();

        ProductPost productPost = productPostRepository.findById(likePostRequest.getProductPostId())
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        ProductPostFile productPostFile = productPostFileRepository.findByProductPost(productPost)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        if (productPost.getUser().equals(requestUser)) {
            throw new CustomException(DO_NOT_LIKE_YOURSELF);
        }


        // 좋아요를 하지 않은 게시글이면 좋아요 추가
        if (!userLikePostService.isAlreadyLike(requestUser.getId(), productPost.getId())) {

            UserLikePost userLikePost = UserLikePost.builder()
                    .user(requestUser)
                    .productPost(productPost)
                    .productPostFile(productPostFile)
                    .createdAt(LocalDateTime.now())
                    .build();
            userLikePostRepository.save(userLikePost);

            return ResponseEntity
                    .status(OK)
                    .body(new HashMap<>() {{
                        put("Like", true);
                    }});
        }

        // 좋아요를 이미 했던 게시글이면 좋아요 삭제
        else if (userLikePostService.isAlreadyLike(requestUser.getId(), productPost.getId())) {
            userLikePostRepository.deleteLikePostByUserId(requestUser.getId(), likePostRequest.getProductPostId());
        }

        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("Like", false);
                }});
    }
}
