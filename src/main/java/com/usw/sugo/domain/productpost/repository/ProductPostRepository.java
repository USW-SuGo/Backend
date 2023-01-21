package com.usw.sugo.domain.productpost.repository;

import com.usw.sugo.domain.productpost.ProductPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPostRepository extends JpaRepository<ProductPost, Long>, CustomProductPostRepository {

}
