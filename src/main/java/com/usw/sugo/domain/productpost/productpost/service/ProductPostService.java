package com.usw.sugo.domain.productpost.productpost.service;

import static com.usw.sugo.global.apiresult.ApiResultFactory.getSuccessFlag;
import static com.usw.sugo.global.exception.ExceptionType.ALREADY_UP_POSTING;
import static com.usw.sugo.global.exception.ExceptionType.CATEGORY_NOT_FOUND;
import static com.usw.sugo.global.exception.ExceptionType.POST_NOT_FOUND;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.ClosePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MyPosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpost.productpostfile.service.ProductPostFileService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.domain.userlikepostnote.UserLikePostAndNoteService;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.util.imagelinkfiltering.ImageLinkCharacterFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductPostService {

    private final ProductPostRepository productPostRepository;
    private final UserServiceUtility userServiceUtility;
    private final ImageLinkCharacterFilter imageLinkCharacterFilter;
    private final UserLikePostAndNoteService userLikePostAndNoteService;
    private final ProductPostFileService productPostFileService;

    public List<MainPageResponse> executeLoadMainPage(Pageable pageable, String category) {
        List<MainPageResponse> mainPageResponses =
            productPostRepository.loadMainPagePostList(pageable, category);
        for (MainPageResponse mainPageResponse : mainPageResponses) {
            mainPageResponse.setLikeCount(loadLikeCountByProductPost(
                loadProductPostById(mainPageResponse.getProductPostId())));
            mainPageResponse.setNoteCount(loadNoteCountByProductPost(
                loadProductPostById(mainPageResponse.getProductPostId())));
            mainPageResponse = imageLinkCharacterFilter.filterImageLink(mainPageResponse);
        }
        return mainPageResponses;
    }

    public List<MyPosting> executeLoadMyPosting(User user, Long userId, Pageable pageable) {
        // 마이 페이지
        if (user.getId().equals(userId)) {
            List<MyPosting> myPostings = productPostRepository.loadWrittenPost(user, pageable);
            for (MyPosting myPosting : myPostings) {
                myPosting.setLikeCount(
                    loadLikeCountByProductPost(loadProductPostById(myPosting.getProductPostId())));
                myPosting.setNoteCount(
                    loadNoteCountByProductPost(loadProductPostById(myPosting.getProductPostId())));
                myPosting = imageLinkCharacterFilter.filterImageLink(myPosting);
            }
            return myPostings;
        }
        // 다른 유저 페이지
        User otherUser = userServiceUtility.loadUserById(userId);
        List<MyPosting> myPostings = productPostRepository.loadWrittenPost(otherUser, pageable);
        for (MyPosting myPosting : myPostings) {
            myPosting.setLikeCount(
                loadLikeCountByProductPost(loadProductPostById(myPosting.getProductPostId())));
            myPosting.setNoteCount(
                loadNoteCountByProductPost(loadProductPostById(myPosting.getProductPostId())));
            myPosting = imageLinkCharacterFilter.filterImageLink(myPosting);
        }
        return myPostings;
    }

    public List<SearchResultResponse> executeSearchPostings(String value, String category) {
        List<SearchResultResponse> searchResultResponses = productPostRepository.searchPost(value,
            validateCategory(category));
        for (SearchResultResponse searchResultResponse : searchResultResponses) {
            searchResultResponse.setLikeCount(loadLikeCountByProductPost(
                loadProductPostById(searchResultResponse.getProductPostId())));
            searchResultResponse.setNoteCount(loadNoteCountByProductPost(
                loadProductPostById(searchResultResponse.getProductPostId())));
            searchResultResponse = imageLinkCharacterFilter.filterImageLink(searchResultResponse);
        }
        return searchResultResponses;
    }

    public DetailPostResponse executeLoadDetailProductPost(Long productPostId, Long userId) {
        DetailPostResponse detailPostResponse =
            productPostRepository.loadDetailPost(productPostId, userId);
        detailPostResponse.setLikeCount(
            loadLikeCountByProductPost(loadProductPostById(detailPostResponse.getProductPostId())));
        detailPostResponse.setNoteCount(
            loadNoteCountByProductPost(loadProductPostById(detailPostResponse.getProductPostId())));
        detailPostResponse = imageLinkCharacterFilter.filterImageLink(detailPostResponse);

        return detailPostResponse;
    }

    public List<ClosePosting> loadClosePosting(User user, Long userId, Pageable pageable) {
        if (user.getId().equals(userId)) {
            List<ClosePosting> closePostings = productPostRepository.loadClosePost(user, pageable);
            String imageLink;
            for (ClosePosting closePosting : closePostings) {
                closePosting.setLikeCount(loadLikeCountByProductPost(
                    loadProductPostById(closePosting.getProductPostId())));
                closePosting.setNoteCount(
                    loadNoteCountByProductPost(
                        loadProductPostById(closePosting.getProductPostId())));
                closePosting = imageLinkCharacterFilter.filterImageLink(closePosting);
            }
            return closePostings;
        }
        User requestUser = userServiceUtility.loadUserById(userId);
        List<ClosePosting> closePostings =
            productPostRepository.loadClosePost(requestUser, pageable);
        String imageLink;
        for (ClosePosting closePosting : closePostings) {
            closePosting.setLikeCount(loadLikeCountByProductPost(
                loadProductPostById(closePosting.getProductPostId())));
            closePosting.setNoteCount(
                loadNoteCountByProductPost(
                    loadProductPostById(closePosting.getProductPostId())));
            closePosting = imageLinkCharacterFilter.filterImageLink(closePosting);
        }
        return closePostings;
    }

    // S3 버킷 객체 생성
    @Transactional
    public Map<String, Boolean> savePosting(Long userId, PostingRequest postingRequest,
        MultipartFile[] multipartFiles) throws IOException {
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
        return getSuccessFlag();
    }

    @Transactional
    public Map<String, Boolean> editPosting(
        ProductPost productPost, String title, String content,
        Integer price, String contactPlace, String category, MultipartFile[] multipartFile) {
        productPost.updateProductPost(title, content, price, contactPlace, category);
        productPostFileService.editProductPostFile(productPost, multipartFile);
        return getSuccessFlag();
    }

    @Transactional
    public Map<String, Boolean> deleteByProductPostId(Long productPostId) {
        ProductPost productPost = loadProductPostById(productPostId);
        productPostFileService.deleteProductPostFileByProductPost(productPost);
        productPostRepository.deleteByEntity(productPost);
        return getSuccessFlag();
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
        return getSuccessFlag();
    }

    @Transactional
    public Map<String, Boolean> closePost(ProductPost productPost) {
        productPost.updateStatusToFalse();
        return getSuccessFlag();
    }

    @Transactional
    public Map<String, Boolean> openPost(ProductPost productPost) {
        productPost.updateStatusToTrue();
        return getSuccessFlag();
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

    public List<ProductPost> loadAllProductPostByUser(User user) {
        return productPostRepository.findAllByUser(user);
    }

    public ProductPost loadProductPostById(Long productPostId) {
        Optional<ProductPost> productPost = productPostRepository.findById(productPostId);
        if (productPost.isPresent()) {
            return productPost.get();
        }
        throw new CustomException(POST_NOT_FOUND);
    }

    private boolean validateUpPostIsAvailable(User user) {
        if (user.getRecentUpPost().isBefore(LocalDateTime.now().minusDays(1))) {
            return true;
        }
        throw new CustomException(ALREADY_UP_POSTING);
    }

    private Integer loadLikeCountByProductPost(ProductPost productPost) {
        return userLikePostAndNoteService.loadLikeCountByProductPost(productPost);
    }

    private Integer loadNoteCountByProductPost(ProductPost productPost) {
        return userLikePostAndNoteService.loadNoteCountByProductPost(productPost);
    }
}
