package com.usw.sugo.domain.productpost.productpost.repository;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPostRepository extends JpaRepository<ProductPost, Long>,
    CustomProductPostRepository {

    List<ProductPost> findAllByUser(User user);
}
