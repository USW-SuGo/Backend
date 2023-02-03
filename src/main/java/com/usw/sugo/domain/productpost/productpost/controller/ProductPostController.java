package com.usw.sugo.domain.productpost.productpost.controller;

import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.DeleteContentRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PutContentRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.UpPostingRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.productpost.productpostfile.repository.ProductPostFileRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ProductPostController {

    private final ProductPostRepository productPostRepository;
    private final ProductPostFileRepository productPostFileRepository;
    private final ProductPostService productPostService;
    private final UserRepository userRepository;
    private final JwtResolver jwtResolver;

    @ResponseStatus(OK)
    @GetMapping("/search")
    public List<SearchResultResponse> searchPost(
            @RequestParam String value, @RequestParam String category) {

        return productPostRepository.searchPost(value, category);
    }

    @ResponseStatus(OK)
    @GetMapping("/all")
    public List<PostResponseDto.MainPageResponse> loadMainPage(
            Pageable pageable, @RequestParam String category) {
        return productPostRepository.loadMainPagePostList(pageable, category);
    }

    @ResponseStatus(OK)
    @GetMapping("/")
    public PostResponseDto.DetailPostResponse loadDetailPost(
            @RequestHeader String authorization,
            @RequestParam long productPostId) {

        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        return productPostRepository.loadDetailPostList(productPostId, userId);
    }

    @ResponseStatus(OK)
    @PostMapping
    public Map<String, Boolean> postContent(
            @RequestHeader String authorization, PostRequestDto.PostingRequest postingRequest,
            @RequestBody MultipartFile[] multipartFileList) throws IOException {

        productPostService.savePosting(
                jwtResolver.jwtResolveToUserId(
                        authorization.substring(7)), postingRequest, multipartFileList);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

//    @ResponseStatus(OK)
//    @PutMapping
//    public Map<String, Boolean> putProductPostAndImage(
//            @RequestBody MultipartFile[] multipartFileList,
//            PutContentRequest putContentRequest) throws IOException {
//        productPostRepository.editPostContent(putContentRequest);
//        String updatedImageLink = productPostService.savePosting(putContentRequest, multipartFileList);
//        productPostFileRepository.editPostFile(updatedImageLink, putContentRequest);
//
//        return new HashMap<>() {{
//            put("Success", true);
//        }};
//    }

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
    public Map<String, Boolean> postContent(
            @RequestHeader String authorization,
            @RequestBody UpPostingRequest upPostingRequest) {

        User requestUser = userRepository.findById(
                jwtResolver.jwtResolveToUserId(authorization.substring(7))).get();

        if (!requestUser.getRecentUpPost().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new CustomException(ExceptionType.ALREADY_UP_POSTING);
        }

        productPostRepository.refreshUpdateAt(upPostingRequest.getProductPostId());
        userRepository.setRecentUpPostingDate(requestUser.getId());

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    @ResponseStatus(OK)
    @PostMapping("/close")
    public Map<String, Boolean> closePost(@RequestBody PostRequestDto.ClosePostRequest closePostRequest) {
        productPostRepository.convertStatus(closePostRequest.getProductPostId());
        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
