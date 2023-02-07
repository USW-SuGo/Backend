package com.usw.sugo.domain.productpost.productpost.controller;

import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.*;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.*;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.user.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ProductPostController {

    private final ProductPostService productPostService;

    @ResponseStatus(OK)
    @GetMapping("/search")
    public List<SearchResultResponse> searchPost(@RequestParam String value, @RequestParam String category) {
        return productPostService.searchPostings(value, category);
    }

    @ResponseStatus(OK)
    @GetMapping("/all")
    public List<MainPageResponse> loadMainPage(@RequestParam String category, Pageable pageable) {
        return productPostService.mainPage(pageable, category);
    }

    @ResponseStatus(OK)
    @GetMapping("/{productPostId}")
    public DetailPostResponse loadDetailPost(@PathVariable Long productPostId, @AuthenticationPrincipal User user) {
        return productPostService.loadDetailProductPost(productPostId, user.getId());
    }

    @ResponseStatus(OK)
    @GetMapping("/my-post")
    public List<MyPosting> loadUserPost(@AuthenticationPrincipal User user, Pageable pageable) {
        return productPostService.loadMyPosting(user, user.getId(), pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/my-post/{userId}")
    public List<MyPosting> loadOtherUserPost(@AuthenticationPrincipal User user, @PathVariable Long userId, Pageable pageable) {
        return productPostService.loadMyPosting(user, userId, pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/close-post")
    public List<ClosePosting> loadUserClosePost(@AuthenticationPrincipal User user, Pageable pageable) {
        return productPostService.loadClosePosting(user, user.getId(), pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/close-post/{userId}")
    public List<ClosePosting> loadUserClosePost(@AuthenticationPrincipal User user, @PathVariable Long userId, Pageable pageable) {
        return productPostService.loadClosePosting(user, userId, pageable);
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
        return productPostService.upPost(user, productPostService.loadProductPostById(upPostingRequest.getProductPostId()));
    }

    @ResponseStatus(OK)
    @PostMapping("/close")
    public Map<String, Boolean> changeStatus(
            @RequestBody ClosePostRequest closePostRequest) {
        return productPostService.closePost(productPostService.loadProductPostById(closePostRequest.getProductPostId()));
    }

    @ResponseStatus(OK)
    @PutMapping
    public Map<String, Boolean> putProductPostAndImage(
            MultipartFile[] multipartFileList,
            PutContentRequest putContentRequest) {
        return productPostService.editPosting(
                productPostService.loadProductPostById(putContentRequest.getProductPostId()),
                putContentRequest.getTitle(), putContentRequest.getContent(), putContentRequest.getPrice(),
                putContentRequest.getContactPlace(), putContentRequest.getCategory(), multipartFileList);
    }

    @ResponseStatus(OK)
    @DeleteMapping
    public Map<String, Boolean> deleteProductPostAndImage(
            @RequestBody DeleteContentRequest deleteContentRequest) {
        return productPostService.deleteByProductPostId(deleteContentRequest.getProductPostId());
    }
}
