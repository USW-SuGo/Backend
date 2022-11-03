package com.usw.sugo.domain.majorproduct.controller;

import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.*;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.majorproduct.repository.productpost.ProductPostRepository;
import com.usw.sugo.domain.majorproduct.service.CommonProductService;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ProductPostController {

    private final ProductPostRepository productPostRepository;
    private final CommonProductService commonProductService;
    private final UserRepository userRepository;
    private final JwtResolver jwtResolver;

    /**
     * 게시글 검색하기
     * @param pageable
     * @param searchValue
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchResultResponse>> searchPost(@RequestParam SearchValue searchValue, Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPostRepository.searchPost(pageable, searchValue.getSearchValue()));
    }

    // 모든 게시물 조회하기
    @GetMapping("/all")
    public ResponseEntity<List<MainPageResponse>> loadMainPage(Pageable pageable,
                                                               @RequestParam(required = false) String category) {

        if (category == null) category = "";

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPostRepository.loadMainPagePostList(pageable, category));
    }

    // 게시글 자세히 보기
    @GetMapping("/")
    public ResponseEntity<DetailPostResponse> loadDetailPost(@RequestHeader String authorization,
                                                             @RequestParam long productPostId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPostRepository.loadDetailPostList(productPostId));
    }


    /**
     * 게시글 작성하기 API
     * @param authorization
     * @param postingRequest
     * @return 게시글 작성 성공여부
     */
    @PostMapping
    public ResponseEntity<HashMap<String, Boolean>> postContent(@RequestHeader String authorization,
                                                                PostingRequest postingRequest,
                                              @RequestBody MultipartFile[] multipartFileList) throws IOException {

        commonProductService.savePosting(authorization, postingRequest, multipartFileList);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{put("Success", true);}});
    }

    /*
    게시글/이미지 수정
     */
    @PutMapping
    public ResponseEntity<Object> editContentImage(@RequestHeader String authorization,
                                                  @RequestBody MultipartFile[] multipartFileList,
                                                  PostingRequest postingRequest) throws IOException {

        StringBuilder imageLinkStringBuilder = commonProductService.savePosting(authorization, postingRequest, multipartFileList);

        productPostRepository.editPostContent(imageLinkStringBuilder, postingRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{put("Success", true);}});
    }
    
    /*
    게시글 삭제
     */

    @DeleteMapping
    public ResponseEntity<Object> deleteContent(@RequestHeader String authorization,
                                             @RequestBody DeleteContentRequest deleteContentRequest) {

        productPostRepository.deleteById(deleteContentRequest.getProductPostId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{put("Success", true);}});
    }


    // 게시글 끌어올리기
    @PostMapping("/up-post")
    public ResponseEntity<Object> postContent(@RequestHeader String authorization,
                                              @RequestBody UpPostingRequest upPostingRequest) {

        User requestUser = userRepository.findById(
                jwtResolver.jwtResolveToUserId(authorization.substring(7))).get();

        if (!requestUser.getRecentUpPost().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new CustomException(ErrorCode.ALREADY_UP_POSTING);
        }

        productPostRepository.refreshUpdateAt(upPostingRequest.getProductPostId());
        userRepository.setRecentUpPostingDate(requestUser.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{put("Success", true);}});
    }

    // 게시글 거래완료 표시하기
    @PostMapping("/close")
    public ResponseEntity<Object> closePost(@RequestHeader String authorization,
                                            @RequestBody ClosePostRequest closePostRequest) {

        User requestUser = userRepository.findById(
                jwtResolver.jwtResolveToUserId(authorization.substring(7))).get();


        productPostRepository.convertStatus(closePostRequest.getProductPostId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{put("Success", true);}});
    }
}
