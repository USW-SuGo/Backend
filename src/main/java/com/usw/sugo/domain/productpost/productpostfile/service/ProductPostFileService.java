package com.usw.sugo.domain.productpost.productpostfile.service;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import com.usw.sugo.domain.productpost.productpostfile.repository.ProductPostFileRepository;
import com.usw.sugo.global.aws.s3.AwsS3ServiceProductPost;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_UPLOAD_EXCEPTION;
import static com.usw.sugo.global.exception.ExceptionType.POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductPostFileService {
    private final ProductPostFileRepository productPostFileRepository;
    private final AwsS3ServiceProductPost awsS3ServiceProductPost;

    public ProductPostFile loadProductPostFileByProductPost(ProductPost productPost) {
        if (productPostFileRepository.findByProductPost(productPost).isPresent()) {
            return productPostFileRepository.findByProductPost(productPost).get();
        }
        throw new CustomException(POST_NOT_FOUND);
    }

    @Transactional
    public List<String> saveProductPostFile(ProductPost productPost, MultipartFile[] multipartFiles) throws IOException {
        List<String> imageLinks = awsS3ServiceProductPost.uploadS3(multipartFiles, productPost.getId());
        ProductPostFile productPostFile = ProductPostFile.builder()
                .productPost(productPost)
                .imageLink(imageLinks.toString())
                .createdAt(LocalDateTime.now())
                .build();
        productPostFileRepository.save(productPostFile);
        return imageLinks;
    }

    @Transactional
    public void deleteProductPostFileByProductPost(ProductPost productPost) {
        awsS3ServiceProductPost.deleteS3ProductPostFile(loadProductPostFileByProductPost(productPost));
        productPostFileRepository.deleteByProductPost(productPost);
    }

    @Transactional
    public void editProductPostFile(ProductPost productPost, MultipartFile[] multipartFiles) {
        Optional<ProductPostFile> productPostFile = productPostFileRepository.findByProductPost(productPost);
        if (productPostFileRepository.findByProductPost(productPost).isEmpty()) {
            throw new CustomException(POST_NOT_FOUND);
        }
        awsS3ServiceProductPost.deleteS3ProductPostFile(loadProductPostFileByProductPost(productPost));
        productPostFileRepository.deleteById(productPostFile.get().getId());
        try {
            saveProductPostFile(productPost, multipartFiles);
        } catch (IOException e) {
            throw new CustomException(INTERNAL_UPLOAD_EXCEPTION);
        }
    }
}
