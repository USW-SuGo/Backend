package com.usw.sugo.domain.majoruser.userlikepost.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.LikePosting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.util.List;

import static com.usw.sugo.domain.majoruser.QUserLikePost.userLikePost;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomUserLikePostRepositoryImpl implements CustomUserLikePostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteLikePostByUserId(long userId, long productPostId) {
        queryFactory
                .delete(userLikePost)
                .where(userLikePost.user.id.eq(userId)
                        .and(userLikePost.productPost.id.eq(productPostId)))
                .execute();
    }

    @Override
    public List<LikePosting> loadMyLikePosting(long userId) {
        return queryFactory
                .select(Projections.bean(LikePosting.class,
                        userLikePost.productPost.id.as("productPostId"),
                        userLikePost.productPostFile.imageLink,
                        userLikePost.productPost.contactPlace,
                        userLikePost.productPost.updatedAt,
                        userLikePost.productPost.title,
                        userLikePost.productPost.price,
                        userLikePost.productPost.category
                        ))
                .from(userLikePost)
                .where(userLikePost.user.id.eq(userId))
                .fetch();
    }
}
