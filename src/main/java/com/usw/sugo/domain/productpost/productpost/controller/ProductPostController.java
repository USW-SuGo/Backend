package com.usw.sugo.domain.productpost.productpost.controller;

import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PutContentRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpost.productpost.service.CommonProductService;
import com.usw.sugo.domain.productpost.productpostfile.repository.ProductPostFileRepository;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ProductPostController {

    private final ProductPostRepository productPostRepository;
    private final ProductPostFileRepository productPostFileRepository;
    private final CommonProductService commonProductService;
    private final UserRepository userRepository;
    private final JwtResolver jwtResolver;

    @GetMapping("/search")
    public ResponseEntity<List<PostResponseDto.SearchResultResponse>> searchPost(
            @RequestParam String value, @RequestParam String category) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPostRepository.searchPost(value, category));
    }

    // 모든 게시물 조회하기
    @GetMapping("/all")
    public ResponseEntity<List<MainPageResponse>> loadMainPage(
            Pageable pageable, @RequestParam String category) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPostRepository.loadMainPagePostList(pageable, category));
    }

    // 게시글 자세히 보기
    @GetMapping("/")
    public ResponseEntity<PostResponseDto.DetailPostResponse> loadDetailPost(
            @RequestHeader String authorization, @RequestParam long productPostId) {

        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPostRepository.loadDetailPostList(productPostId, userId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> postContent(
            @RequestHeader String authorization, PostingRequest postingRequest,
            @RequestBody MultipartFile[] multipartFileList) throws IOException {

        commonProductService.savePosting(authorization, postingRequest, multipartFileList);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    @PutMapping
    public ResponseEntity<Object> putProductPostAndImage(
            @RequestBody MultipartFile[] multipartFileList, PutContentRequest putContentRequest) throws IOException {
        productPostRepository.editPostContent(putContentRequest);
        String updatedImageLink = commonProductService.updateS3Content(putContentRequest, multipartFileList);
        productPostFileRepository.editPostFile(updatedImageLink, putContentRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteProductPostAndImage(
            @RequestBody PostRequestDto.DeleteContentRequest deleteContentRequest) {
        commonProductService.deleteS3Content(deleteContentRequest.getProductPostId());
        productPostRepository.deleteById(deleteContentRequest.getProductPostId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }


    // 게시글 끌어올리기
    @PostMapping("/up-post")
    public ResponseEntity<Object> postContent(@RequestHeader String authorization,
                                              @RequestBody PostRequestDto.UpPostingRequest upPostingRequest) {

        User requestUser = userRepository.findById(
                jwtResolver.jwtResolveToUserId(authorization.substring(7))).get();

        if (!requestUser.getRecentUpPost().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new CustomException(ErrorCode.ALREADY_UP_POSTING);
        }

        productPostRepository.refreshUpdateAt(upPostingRequest.getProductPostId());
        userRepository.setRecentUpPostingDate(requestUser.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    // 게시글 거래완료 표시하기
    @PostMapping("/close")
    public ResponseEntity<Object> closePost(@RequestBody PostRequestDto.ClosePostRequest closePostRequest) {

        productPostRepository.convertStatus(closePostRequest.getProductPostId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }
}
