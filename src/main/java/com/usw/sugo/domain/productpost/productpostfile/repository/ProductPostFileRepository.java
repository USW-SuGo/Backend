package com.usw.sugo.domain.productpost.productpostfile.repository;

import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.productpost.ProductPostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPostFileRepository extends JpaRepository<ProductPostFile, Long>, CustomProductPostFileRepository {

    Optional<ProductPostFile> findByProductPost(ProductPost productPost);
}
