package com.usw.sugo.domain.productpost.productpostfile.repository;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomProductPostFileRepository {

    void deleteByProductPost(ProductPost productPost);
}
