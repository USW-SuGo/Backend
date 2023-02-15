package com.usw.sugo.domain.user.userlikepost.repository;

import static com.usw.sugo.domain.productpost.productpost.QProductPost.productPost;
import static com.usw.sugo.domain.user.userlikepost.QUserLikePost.userLikePost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.LikePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.QPostResponseDto_LikePosting;
import com.usw.sugo.domain.user.userlikepost.UserLikePost;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
@Transactional
public class CustomUserLikePostRepositoryImpl implements CustomUserLikePostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean checkUserLikeStatusForPost(Long userId, Long productPostId) {
        List<UserLikePost> fetch = queryFactory
            .selectFrom(userLikePost)
            .where(userLikePost.user.id.eq(userId)
                .and(userLikePost.productPost.id.eq(productPostId)))
            .fetch();
        return fetch.size() > 0;
    }

    @Override
    public void deleteLikePostByUserId(Long userId, Long productPostId) {
        queryFactory
            .delete(userLikePost)
            .where(userLikePost.user.id.eq(userId)
                .and(userLikePost.productPost.id.eq(productPostId)))
            .execute();
    }

    @Override
    public List<LikePosting> loadMyLikePosting(Long userId, Pageable pageable) {
        return queryFactory
            .select(new QPostResponseDto_LikePosting(
                userLikePost.productPost.id.as("productPostId"),
                userLikePost.productPostFile.imageLink,
                userLikePost.productPost.contactPlace,
                userLikePost.productPost.updatedAt,
                userLikePost.productPost.title,
                userLikePost.productPost.price,
                userLikePost.productPost.category,
                userLikePost.productPost.status
            ))
            .from(userLikePost)
            .where(userLikePost.user.id.eq(userId))
            .orderBy(productPost.updatedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
