package com.usw.sugo.domain.user.userlikepost.repository;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.userlikepost.UserLikePost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikePostRepository extends JpaRepository<UserLikePost, Long>,
    CustomUserLikePostRepository {

    void deleteByUser(User user);
}
