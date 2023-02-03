package com.usw.sugo.domain.productpost.productpost.repository;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPostRepository extends JpaRepository<ProductPost, Long>, CustomProductPostRepository {
    List<ProductPost> findAllByUser(User user);
}
