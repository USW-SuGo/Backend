package com.usw.sugo.domain.productpost.productpost.controller;

import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.*;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.user.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
    public List<SearchResultResponse> searchPost(
            @RequestParam String value, @RequestParam String category) {
        return productPostService.searchPostings(value, category);
    }

    @ResponseStatus(OK)
    @GetMapping("/all")
    public List<MainPageResponse> loadMainPage(
            Pageable pageable, @RequestParam String category) {
        return productPostService.mainPage(pageable, category);
    }

    @ResponseStatus(OK)
    @GetMapping("/{productPostId}")
    public DetailPostResponse loadDetailPost(
            @RequestHeader String authorization,
            @PathVariable Long productPostId,
            @AuthenticationPrincipal User user) {
        return productPostService.loadDetailProductPost(productPostId, user.getId());
    }

    @ResponseStatus(OK)
    @PostMapping
    public Map<String, Boolean> savePost(
            @RequestHeader String authorization,
            @RequestBody PostingRequest postingRequest,
            @AuthenticationPrincipal User user,
            MultipartFile[] multipartFileList) throws IOException {
        return productPostService.savePosting(user.getId(), postingRequest, multipartFileList);
    }

    @ResponseStatus(OK)
    @PutMapping
    public Map<String, Boolean> putProductPostAndImage(
            @RequestBody MultipartFile[] multipartFileList,
            PutContentRequest putContentRequest) {
        return productPostService.editPosting(
                productPostService.loadProductPost(putContentRequest.getProductPostId()),
                putContentRequest.getTitle(), putContentRequest.getContent(), putContentRequest.getPrice(),
                putContentRequest.getContactPlace(), putContentRequest.getCategory(), multipartFileList);
    }

    @ResponseStatus(OK)
    @DeleteMapping
    public Map<String, Boolean> deleteProductPostAndImage(
            @RequestBody DeleteContentRequest deleteContentRequest) {
        productPostService.deleteByProductPostId(deleteContentRequest.getProductPostId());
        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    @ResponseStatus(OK)
    @PostMapping("/up-post")
    public Map<String, Boolean> upPost(
            @RequestHeader String authorization,
            @RequestBody UpPostingRequest upPostingRequest,
            @AuthenticationPrincipal User user) {
        return productPostService.upPost(user, productPostService.loadProductPost(upPostingRequest.getProductPostId()));
    }

    @ResponseStatus(OK)
    @PostMapping("/status")
    public Map<String, Boolean> changeStatus(
            @RequestBody ClosePostRequest closePostRequest) {
        return productPostService.closePost(productPostService.loadProductPost(closePostRequest.getProductPostId()));
    }
}
