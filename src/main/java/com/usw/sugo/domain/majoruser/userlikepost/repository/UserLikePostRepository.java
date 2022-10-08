package com.usw.sugo.domain.majoruser.userlikepost.repository;

import com.usw.sugo.domain.majoruser.UserLikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikePostRepository extends JpaRepository<UserLikePost, Long>, CustomUserLikePostRepository {
}
