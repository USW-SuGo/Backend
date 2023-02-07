package com.usw.sugo.domain.productpost.productpost.service;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.*;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpost.productpostfile.service.ProductPostFileService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.usw.sugo.domain.ApiResult.SUCCESS;
import static com.usw.sugo.global.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductPostService {

    private final UserServiceUtility userServiceUtility;
    private final ProductPostRepository productPostRepository;
    private final ProductPostFileService productPostFileService;

    private final Map<String, Boolean> successFlag = new HashMap<>() {{
        put(SUCCESS.getResult(), true);
    }};

    public List<MainPageResponse> mainPage(Pageable pageable, String category) {
        List<MainPageResponse> mainPageResponses = productPostRepository.loadMainPagePostList(pageable, category);
        for (MainPageResponse mainPageResponse : mainPageResponses) {
            if (mainPageResponse.getImageLink() == null) {
                mainPageResponse.setImageLink("");
            } else {
                String imageLink = mainPageResponse.getImageLink()
                        .replace("[", "")
                        .replace("]", "");
                mainPageResponse.setImageLink(imageLink);
            }
        }
        return mainPageResponses;
    }

    public List<MyPosting> loadMyPosting(User user, Long userId, Pageable pageable) {
        // 마이 페이지
        if (user.getId().equals(userId)) {
            List<MyPosting> myPostings = productPostRepository.loadWrittenPost(user, pageable);
            String imageLink;
            for (MyPosting myPosting : myPostings) {
                imageLink = myPosting.getImageLink()
                        .split(",")[0]
                        .replace("[", "")
                        .replace("]", "");
                myPosting.setImageLink(imageLink);
            }
            return myPostings;
        }
        // 다른 유저 페이지
        User otherUser = userServiceUtility.loadUserById(userId);
        List<MyPosting> myPostings = productPostRepository.loadWrittenPost(otherUser, pageable);
        String imageLink;
        for (MyPosting myPosting : myPostings) {
            imageLink = myPosting.getImageLink()
                    .split(",")[0]
                    .replace("[", "")
                    .replace("]", "");
            myPosting.setImageLink(imageLink);
        }
        return myPostings;
    }

    public List<SearchResultResponse> searchPostings(String value, String category) {
        List<SearchResultResponse> searchResultResponses = productPostRepository.searchPost(value, validateCategory(category));
        for (SearchResultResponse searchResultResponse : searchResultResponses) {
            if (searchResultResponse.getImageLink() == null) {
                searchResultResponse.setImageLink("");
            } else {
                String imageLink = searchResultResponse.getImageLink()
                        .replace("[", "")
                        .replace("]", "");
                searchResultResponse.setImageLink(imageLink);
            }
        }
        return searchResultResponses;
    }

    public ProductPost loadProductPostById(Long productPostId) {
        Optional<ProductPost> productPost = productPostRepository.findById(productPostId);
        if (productPost.isPresent()) {
            return productPost.get();
        }
        throw new CustomException(POST_NOT_FOUND);
    }

    public DetailPostResponse loadDetailProductPost(Long productPostId, Long userId) {
        DetailPostResponse detailPostResponse = productPostRepository.loadDetailPost(productPostId, userId);
        if (detailPostResponse.getImageLink() == null) {
            detailPostResponse.setImageLink("");
        } else {
            String imageLink = detailPostResponse.getImageLink()
                    .replace("[", "")
                    .replace("]", "");
            detailPostResponse.setImageLink(imageLink);
        }
        return detailPostResponse;
    }

    public List<ProductPost> loadAllProductPostByUser(User user) {
        return productPostRepository.findAllByUser(user);
    }

    public List<ClosePosting> loadClosePosting(User user, Long userId, Pageable pageable) {
        if (user.getId().equals(userId)) {
            List<ClosePosting> closePostings = productPostRepository.loadClosePost(user, pageable);
            String imageLink;
            for (ClosePosting closePosting : closePostings) {
                imageLink = closePosting.getImageLink()
                        .split(",")[0]
                        .replace("[", "")
                        .replace("]", "");
                closePosting.setImageLink(imageLink);
            }
            return closePostings;
        }
        User requestUser = userServiceUtility.loadUserById(userId);
        List<ClosePosting> closePostings = productPostRepository.loadClosePost(requestUser, pageable);
        String imageLink;
        for (ClosePosting closePosting : closePostings) {
            imageLink = closePosting.getImageLink()
                    .split(",")[0]
                    .replace("[", "")
                    .replace("]", "");
            closePosting.setImageLink(imageLink);
        }
        return closePostings;
    }

    // S3 버킷 객체 생성
    @Transactional
    public Map<String, Boolean> savePosting(Long userId, PostingRequest postingRequest, MultipartFile[] multipartFiles) throws IOException {
        User requestUser = userServiceUtility.loadUserById(userId);
        ProductPost productPost = ProductPost.builder()
                .user(requestUser)
                .title(postingRequest.getTitle())
                .content(postingRequest.getContent())
                .price(postingRequest.getPrice())
                .contactPlace(postingRequest.getContactPlace())
                .category(postingRequest.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(true)
                .build();
        productPostRepository.save(productPost);
        productPostFileService.saveProductPostFile(productPost, multipartFiles);
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> editPosting(
            ProductPost productPost, String title, String content,
            Integer price, String contactPlace, String category, MultipartFile[] multipartFile) {
        productPost.updateProductPost(title, content, price, contactPlace, category);
        productPostRepository.save(productPost);
        productPostFileService.editProductPostFile(productPost, multipartFile);
        return successFlag;
    }

    @Transactional
    public void deleteByProductPostId(Long productPostId) {
        ProductPost productPost = loadProductPostById(productPostId);
        productPostFileService.deleteProductPostFileByProductPost(productPost);
        productPostRepository.deleteByEntity(productPost);
    }

    @Transactional
    public void deleteByUser(User user) {
        List<ProductPost> productPosts = loadAllProductPostByUser(user);
        for (ProductPost productPost : productPosts) {
            productPostFileService.deleteProductPostFileByProductPost(productPost);
            productPostRepository.delete(productPost);
        }
    }

    @Transactional
    public Map<String, Boolean> upPost(User user, ProductPost productPost) {
        if (validateUpPostIsAvailable(user)) {
            User requestUser = userServiceUtility.loadUserById(user.getId());
            requestUser.updateRecentUpPost();
            productPost.updateUpdatedAt();
        }
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> closePost(ProductPost productPost) {
        productPost.updateStatusToFalse();
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> openPost(ProductPost productPost) {
        productPost.updateStatusToTrue();
        return successFlag;
    }

    private String validateCategory(String category) {
        if (category.equals("서적") ||
                category.equals("생활용품") ||
                category.equals("전자기기") ||
                category.equals("기타") ||
                category.equals("")) {
            return category;
        }
        throw new CustomException(CATEGORY_NOT_FOUND);
    }

    private boolean validateUpPostIsAvailable(User user) {
        if (user.getRecentUpPost().isBefore(LocalDateTime.now().minusDays(1))) {
            return true;
        }
        throw new CustomException(ALREADY_UP_POSTING);
    }
}
