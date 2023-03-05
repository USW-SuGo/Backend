package com.usw.sugo.domain.productpost.productpost.controller;

import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.productpost.productpost.controller.dto.PostRequestDto.ClosePostRequest;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostRequestDto.DeleteContentRequest;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostRequestDto.PutContentRequest;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostRequestDto.UpPostingRequest;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.ClosePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MyPosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostServiceUtility;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.userlikepost.service.UserLikePostService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ProductPostController {

    private final ProductPostServiceUtility productPostServiceUtility;
    private final ProductPostService productPostService;

    @ResponseStatus(OK)
    @GetMapping("/search")
    public List<SearchResultResponse> searchPost(
        @RequestParam String value,
        @RequestParam String category,
        Pageable pageable) {
        return productPostService.executeSearchPostings(value, category, pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/all")
    public List<MainPageResponse> loadMainPage(@RequestParam String category, Pageable pageable) {
        return productPostService.executeLoadMainPage(pageable, category);
    }

    @ResponseStatus(OK)
    @GetMapping("/{productPostId}")
    public DetailPostResponse loadDetailPost(@PathVariable Long productPostId,
        @AuthenticationPrincipal User user) {
        return productPostService.executeLoadDetailProductPost(productPostId, user.getId());
    }

    @ResponseStatus(OK)
    @GetMapping("/my-post")
    public List<MyPosting> loadUserPost(@AuthenticationPrincipal User user, Pageable pageable) {
        return productPostService.executeLoadMyPosting(user, user.getId(), pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/my-post/{userId}")
    public List<MyPosting> loadOtherUserPost(@AuthenticationPrincipal User user,
        @PathVariable Long userId, Pageable pageable) {
        return productPostService.executeLoadMyPosting(user, userId, pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/close-post")
    public List<ClosePosting> loadUserClosePost(@AuthenticationPrincipal User user,
        Pageable pageable) {
        return productPostService.executeLoadClosePosting(user, user.getId(), pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/close-post/{userId}")
    public List<ClosePosting> loadUserClosePost(@AuthenticationPrincipal User user,
        @PathVariable Long userId, Pageable pageable) {
        return productPostService.executeLoadClosePosting(user, userId, pageable);
    }

    @ResponseStatus(OK)
    @PostMapping
    public Map<String, Boolean> savePost(
        PostingRequest postingRequest,
        @RequestBody MultipartFile[] multipartFileList,
        @AuthenticationPrincipal User user) throws IOException {
        return productPostService.savePosting(user.getId(), postingRequest, multipartFileList);
    }

    @ResponseStatus(OK)
    @PostMapping("/up-post")
    public Map<String, Boolean> upPost(
        @RequestBody UpPostingRequest upPostingRequest,
        @AuthenticationPrincipal User user) {
        return productPostService.upPost(user,
            productPostServiceUtility.loadProductPostById(upPostingRequest.getProductPostId()));
    }

    @ResponseStatus(OK)
    @PostMapping("/close")
    public Map<String, Boolean> changeStatus(
        @RequestBody ClosePostRequest closePostRequest) {
        return productPostService.closePost(
            productPostServiceUtility.loadProductPostById(closePostRequest.getProductPostId()));
    }

    @ResponseStatus(OK)
    @PutMapping
    public Map<String, Boolean> putProductPostAndImage(
        PutContentRequest putContentRequest,
        @RequestBody MultipartFile[] multipartFileList) {
        return productPostService.editPosting(
            productPostServiceUtility.loadProductPostById(putContentRequest.getProductPostId()),
            putContentRequest.getTitle(), putContentRequest.getContent(),
            putContentRequest.getPrice(),
            putContentRequest.getContactPlace(), putContentRequest.getCategory(),
            multipartFileList);
    }

    @ResponseStatus(OK)
    @DeleteMapping
    public Map<String, Boolean> deleteProductPost(
        @RequestBody DeleteContentRequest deleteContentRequest,
        @AuthenticationPrincipal User user) {
        return productPostService.deleteByProductPostId(
            deleteContentRequest.getProductPostId(),
            user
        );
    }
}
