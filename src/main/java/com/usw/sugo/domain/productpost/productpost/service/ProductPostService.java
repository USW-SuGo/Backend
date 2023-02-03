package com.usw.sugo.domain.productpost.productpost.service;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpost.productpostfile.service.ProductPostFileService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.MyPosting;
import com.usw.sugo.domain.user.user.service.UserService;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductPostService {

    private final UserService userService;
    private final ProductPostRepository productPostRepository;
    private final ProductPostFileService productPostFileService;

    public ProductPost loadProductPost(Long productPostId) {
        if (productPostRepository.findById(productPostId).isPresent()) {
            return productPostRepository.findById(productPostId).get();
        }
        throw new CustomException(ExceptionType.POST_NOT_FOUND);
    }

    public List<ProductPost> loadAllProductPostByUser(User user) {
        return productPostRepository.findAllByUser(user);
    }

    public List<MyPosting> loadUserWritingPostingList(User user, Pageable pageable) {
        return productPostRepository.loadUserWritingPostingList(user, pageable);
    }

    // S3 버킷 객체 생성
    @Transactional
    public void savePosting(Long userId, PostingRequest postingRequest, MultipartFile[] multipartFiles) throws IOException {
        User requestUser = userService.loadUserById(userId);
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
}
