package com.usw.sugo.domain.productpost.productpost.service;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpost.productpostfile.service.ProductPostFileService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.MyPosting;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.global.exception.ExceptionType.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductPostService {

    private final UserServiceUtility userServiceUtility;
    private final ProductPostRepository productPostRepository;
    private final ProductPostFileService productPostFileService;

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

    public List<MyPosting> myPostings(User user, Pageable pageable) {
        List<MyPosting> myPostings = productPostRepository.loadUserWritingPostingList(user, pageable);
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

    public ProductPost loadProductPost(Long productPostId) {
        if (productPostRepository.findById(productPostId).isPresent()) {
            return productPostRepository.findById(productPostId).get();
        }
        throw new CustomException(ExceptionType.POST_NOT_FOUND);
    }

    public DetailPostResponse loadDetailProductPost(Long productPostId, Long userId) {
        return productPostRepository.loadDetailPost(productPostId, userId);
    }

    public List<ProductPost> loadAllProductPostByUser(User user) {
        return productPostRepository.findAllByUser(user);
    }

    // S3 버킷 객체 생성
    @Transactional
    public void savePosting(Long userId, PostingRequest postingRequest, MultipartFile[] multipartFiles) throws IOException {
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
    }

    @Transactional
    public void deleteByProductPostId(Long productPostId) {
        ProductPost productPost = loadProductPost(productPostId);
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

    public String validateCategory(String category) {
        if (category.equals("서적") ||
                category.equals("생활용품") ||
                category.equals("전자기기") ||
                category.equals("기타") ||
                category.equals("")) {
            return category;
        }
        throw new CustomException(CATEGORY_NOT_FOUND);
    }
}
