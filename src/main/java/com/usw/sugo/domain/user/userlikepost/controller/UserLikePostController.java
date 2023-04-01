package com.usw.sugo.domain.user.userlikepost.controller;

import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.LikePosting;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.userlikepost.dto.UserLikePostRequestDto.LikePostRequest;
import com.usw.sugo.domain.user.userlikepost.service.UserLikePostService;
import com.usw.sugo.global.annotation.ApiLogger;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like-post")
public class UserLikePostController {

    private final UserLikePostService userLikePostService;

    @ApiLogger
    @PostMapping
    public Map<String, Boolean> likePost(
        @RequestBody @Valid LikePostRequest likePostRequest,
        @AuthenticationPrincipal User user
    ) {
        return userLikePostService.executeLikeUnlikePost(user.getId(),
            likePostRequest.getProductPostId());
    }

    @ApiLogger
    @ResponseStatus(OK)
    @GetMapping
    public List<LikePosting> loadUserLikePost(
        @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        return userLikePostService.loadLikePosts(user.getId(), pageable);
    }
}
