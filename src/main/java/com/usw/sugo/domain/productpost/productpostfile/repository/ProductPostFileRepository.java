package com.usw.sugo.domain.productpost.productpostfile.repository;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPostFileRepository extends JpaRepository<ProductPostFile, Long>,
    CustomProductPostFileRepository {


    @Query("SELECT ppf FROM ProductPostFile ppf WHERE ppf.productPost = :productPost")
    Optional<ProductPostFile> findByProductPost(@Param("productPost")ProductPost productPost);
}
