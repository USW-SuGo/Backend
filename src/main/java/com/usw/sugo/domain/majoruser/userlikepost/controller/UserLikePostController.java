package com.usw.sugo.domain.majoruser.userlikepost.controller;

import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majorproduct.ProductPostFile;
import com.usw.sugo.domain.majorproduct.repository.productpost.ProductPostRepository;
import com.usw.sugo.domain.majorproduct.repository.productpostfile.ProductPostFileRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.UserLikePost;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.majoruser.userlikepost.dto.UserLikePostRequestDto.LikePostRequest;
import com.usw.sugo.domain.majoruser.userlikepost.repository.UserLikePostRepository;
import com.usw.sugo.domain.majoruser.userlikepost.service.UserLikePostService;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        ProductPostFile productPostFile = productPostFileRepository.findByProductPost(productPost)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        
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
                    .body(new HashMap<>() {{put("Like", true);
                    }});
        }

        // 좋아요를 이미 했던 게시글이면 좋아요 삭제
        else if (userLikePostService.isAlreadyLike(requestUser.getId(), productPost.getId())) {
            userLikePostRepository.deleteLikePostByUserId(requestUser.getId(), likePostRequest.getProductPostId());
        }

        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{put("Like", false);
        }});
    }
}
