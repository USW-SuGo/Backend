package com.usw.sugo.domain.majorproduct.repository.productpostfile;

import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majorproduct.ProductPostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPostFileRepository extends JpaRepository<ProductPostFile, Long>, CustomProductPostFileRepository {

    Optional<ProductPostFile> findByProductPost(ProductPost productPost);
}
