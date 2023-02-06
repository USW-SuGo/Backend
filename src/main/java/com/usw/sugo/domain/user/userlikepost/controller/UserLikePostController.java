package com.usw.sugo.domain.user.userlikepost.controller;

import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.LikePosting;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.userlikepost.dto.UserLikePostRequestDto.LikePostRequest;
import com.usw.sugo.domain.user.userlikepost.service.UserLikePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like-post")
public class UserLikePostController {
    private final UserLikePostService userLikePostService;

    @PostMapping
    public Map<String, Boolean> likePost(
            @RequestHeader String authorization,
            @RequestBody LikePostRequest likePostRequest,
            @AuthenticationPrincipal User user) {
        return userLikePostService.executeLikeUnlikePost(user.getId(), likePostRequest.getProductPostId());
    }

    @ResponseStatus(OK)
    @GetMapping
    public List<LikePosting> loadUserLikePost(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return userLikePostService.loadLikePosts(user.getId(), pageable);
    }
}
