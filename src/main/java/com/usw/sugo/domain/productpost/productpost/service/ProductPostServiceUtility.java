package com.usw.sugo.domain.productpost.productpost.service;

import static com.usw.sugo.global.exception.ExceptionType.POST_NOT_FOUND;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.global.exception.CustomException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductPostServiceUtility {

    private final ProductPostRepository productPostRepository;

    public ProductPost loadProductPostById(Long productPostId) {
        final Optional<ProductPost> productPost = productPostRepository.findById(productPostId);
        if (productPost.isPresent()) {
            return productPost.get();
        }
        throw new CustomException(POST_NOT_FOUND);
    }
}
