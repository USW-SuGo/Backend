package com.usw.sugo.domain.productpost.productpost.repository;

import static com.usw.sugo.domain.productpost.productpost.QProductPost.productPost;
import static com.usw.sugo.domain.productpost.productpostfile.QProductPostFile.productPostFile;
import static com.usw.sugo.domain.user.user.QUser.user;
import static com.usw.sugo.domain.user.userlikepost.QUserLikePost.userLikePost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.ClosePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MyPosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.QPostResponseDto_ClosePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.QPostResponseDto_DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.QPostResponseDto_MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.QPostResponseDto_MyPosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.QPostResponseDto_SearchResultResponse;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostRepositoryImpl implements CustomProductPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByEntity(ProductPost requestProductPost) {
        queryFactory
            .delete(productPost)
            .where(productPost.id.eq(requestProductPost.getId()))
            .execute();
    }

    @Override
    public List<SearchResultResponse> searchPost(String value, String category, Pageable pageable) {
        List<SearchResultResponse> response;
        if (category.equals("")) {
            response = queryFactory
                .select(new QPostResponseDto_SearchResultResponse(
                    productPost.id,
                    productPostFile.imageLink,
                    productPost.contactPlace, productPost.updatedAt,
                    productPost.title, productPost.price,
                    productPost.user.nickname, productPost.category, productPost.status))
                .from(productPost, productPostFile)
                .join(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                .join(user).on(productPost.user.id.eq(user.id))
                .where(productPost.title.contains(value)
                    .and(productPost.status.isTrue()))
                .orderBy(productPost.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        } else {
            response = queryFactory
                .select(new QPostResponseDto_SearchResultResponse(
                    productPost.id,
                    productPostFile.imageLink,
                    productPost.contactPlace, productPost.updatedAt,
                    productPost.title, productPost.price,
                    productPost.user.nickname, productPost.category, productPost.status))
                .from(productPost, productPostFile)
                .join(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                .join(user).on(productPost.user.id.eq(user.id))
                .where(productPost.title.contains(value)
                    .and(productPost.category.eq(category))
                    .and(productPost.status.isTrue()))
                .orderBy(productPost.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        }
        return response;
    }

    @Override
    public List<MainPageResponse> loadMainPagePostList(Pageable pageable, String inputCategory) {
        List<MainPageResponse> response;
        if (inputCategory.equals("")) {
            response = queryFactory
                .select(new QPostResponseDto_MainPageResponse(
                    productPost.id,
                    productPostFile.imageLink,
                    productPost.contactPlace, productPost.updatedAt,
                    productPost.title, productPost.price,
                    productPost.user.nickname, productPost.category, productPost.status))
                .from(productPost)
                .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                .where(productPost.status.isTrue())
                .orderBy(productPost.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        } else {
            response = queryFactory
                .select(new QPostResponseDto_MainPageResponse(
                    productPost.id,
                    productPostFile.imageLink,
                    productPost.contactPlace, productPost.updatedAt,
                    productPost.title, productPost.price,
                    productPost.user.nickname, productPost.category, productPost.status))
                .from(productPost)
                .where(productPost.category.eq(inputCategory))
                .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                .where(productPost.status.isTrue())
                .orderBy(productPost.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        }
        return response;
    }

    @Override
    public DetailPostResponse loadDetailPost(Long productPostId, Long userId) {
        DetailPostResponse response = queryFactory
            .select(new QPostResponseDto_DetailPostResponse(
                productPost.id.as("productPostId"), productPost.user.id.as("writerId"),
                productPostFile.imageLink, productPost.contactPlace, productPost.updatedAt,
                productPost.title, productPost.content, productPost.price,
                productPost.user.nickname, productPost.category, productPost.status))
            .from(productPost)
            .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
            .where(productPost.id.eq(productPostId))
            .fetchOne();

        long count = queryFactory
            .selectFrom(userLikePost)
            .join(productPost)
            .on(productPost.id.eq(userLikePost.productPost.id))
            .where(userLikePost.user.id.eq(userId)
                .and(userLikePost.productPost.id.eq(productPostId)))
            .stream().count();
        if (count != 0) {
            response.setUserLikeStatus(true);
            return response;
        }
        response.setUserLikeStatus(false);
        return response;
    }

    @Override
    public List<MyPosting> loadWrittenPost(User user, Pageable pageable) {
        return queryFactory
            .select(new QPostResponseDto_MyPosting(
                productPost.id.as("productPostId"), productPostFile.imageLink,
                productPost.contactPlace,
                productPost.updatedAt, productPost.title, productPost.price, productPost.category,
                productPost.status))
            .from(productPost)
            .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
            .where(productPost.user.eq(user)
                .and(productPost.status.isTrue()))
            .orderBy(productPost.updatedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public List<ClosePosting> loadClosePost(User user, Pageable pageable) {
        return queryFactory
            .select(new QPostResponseDto_ClosePosting(
                productPost.id.as("productPostId"), productPostFile.imageLink,
                productPost.contactPlace,
                productPost.updatedAt, productPost.title, productPost.price, productPost.category,
                productPost.status))
            .from(productPost)
            .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
            .where(productPost.user.eq(user)
                .and(productPost.status.isFalse()))
            .orderBy(productPost.updatedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
