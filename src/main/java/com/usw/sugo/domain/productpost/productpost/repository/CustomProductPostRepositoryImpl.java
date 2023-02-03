package com.usw.sugo.domain.productpost.productpost.repository;

import com.amazonaws.util.StringUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.dto.QPostResponseDto_DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.dto.QPostResponseDto_MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.dto.QPostResponseDto_SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.service.CategoryValidator;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.QUserResponseDto_MyPosting;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.MyPosting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.usw.sugo.domain.productpost.productpost.QProductPost.productPost;
import static com.usw.sugo.domain.productpost.productpostfile.QProductPostFile.productPostFile;
import static com.usw.sugo.domain.user.userlikepost.QUserLikePost.userLikePost;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostRepositoryImpl implements CustomProductPostRepository {

    private final JPAQueryFactory queryFactory;

    private BooleanExpression extractCategory(String category) {
        if (StringUtils.isNullOrEmpty(category)) {
            return null;
        }
        return productPost.category.eq(category);
    }

    @Override
    public void deleteByEntity(ProductPost requestProductPost) {
        queryFactory
                .delete(productPost)
                .where(productPost.eq(requestProductPost))
                .execute();
    }

    @Override
    public List<SearchResultResponse> searchPost(String value, String category) {
        List<SearchResultResponse> response = new ArrayList<>();

        List<SearchResultResponse> fetch = queryFactory
                .select(new QPostResponseDto_SearchResultResponse(
                        productPost.id,
                        productPostFile.imageLink,
                        productPost.contactPlace, productPost.updatedAt,
                        productPost.title, productPost.price,
                        productPost.user.nickname, productPost.category, productPost.status))
                .from(productPost)
                .where(productPost.title.contains(value))
                .fetchJoin()
                //.on(productPostFile.productPost.id.eq(productPost.id))
                .orderBy(productPost.updatedAt.desc())
                .fetch();

        System.out.println("test2");

        if (category.equals("")) {
            response = queryFactory
                    .select(new QPostResponseDto_SearchResultResponse(
                            productPost.id,
                            productPostFile.imageLink,
                            productPost.contactPlace, productPost.updatedAt,
                            productPost.title, productPost.price,
                            productPost.user.nickname, productPost.category, productPost.status))
                    .from(productPost)
                    .where(productPost.title.contains(value))
                    .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                    .orderBy(productPost.updatedAt.desc())
                    .fetch();
        } else if (CategoryValidator.validateCategory(category)) {
            response = queryFactory
                    .select(new QPostResponseDto_SearchResultResponse(
                            productPost.id,
                            productPostFile.imageLink,
                            productPost.contactPlace, productPost.updatedAt,
                            productPost.title, productPost.price,
                            productPost.user.nickname, productPost.category, productPost.status))
                    .from(productPost)
                    .where(productPost.title.contains(value)
                            .and(productPost.category.eq(category))
                            .and(extractCategory(category))
                    )
                    .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                    .orderBy(productPost.updatedAt.desc())
                    .fetch();
        }

        int listSize = response.size();
        String[] imageList;

        for (int i = 0; i < listSize; i++) {
            if (response.get(i).getImageLink() == null) {
                response.get(i).setImageLink("");
            } else if (response.get(i).getImageLink() != null) {
                imageList = response.get(i).getImageLink().split(",");
                response.get(i).setImageLink(Arrays.toString(imageList));
            }
        }

        return response;
    }

    @Override
    public List<MainPageResponse> loadMainPagePostList(Pageable pageable, String inputCategory) {
        List<MainPageResponse> response = new ArrayList<>();
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
                    .orderBy(productPost.updatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (CategoryValidator.validateCategory(inputCategory)) {
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
                    .orderBy(productPost.updatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }
        int listSize = response.size();
        String[] imageList;
        for (MainPageResponse mainPageResponse : response) {
            if (mainPageResponse.getImageLink() == null) {
                mainPageResponse.setImageLink("");
            } else if (mainPageResponse.getImageLink() != null) {
                imageList = mainPageResponse.getImageLink().split(",");
                mainPageResponse.setImageLink(Arrays.toString(imageList));
            }
        }
        return response;
    }

    @Override
    public DetailPostResponse loadDetailPostList(long productPostId, long userId) {
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

        if (count == 0) {
            response.setUserLikeStatus(false);
            String[] imageList;
            imageList = response.getImageLink().split(",");
            response.setImageLink(Arrays.toString(imageList));
            return response;
        }
        response.setUserLikeStatus(true);
        String[] imageList;
        imageList = response.getImageLink().split(",");
        response.setImageLink(Arrays.toString(imageList));
        return response;
    }

    // 유저 페이지 조회 (마이페이지 포함)
    @Override
    public List<MyPosting> loadUserWritingPostingList(User user, Pageable pageable) {
        List<MyPosting> response = queryFactory
                .select(new QUserResponseDto_MyPosting(
                        productPost.id.as("productPostId"), productPostFile.imageLink, productPost.contactPlace,
                        productPost.updatedAt, productPost.title, productPost.price, productPost.category,
                        productPost.status))
                .from(productPost)
                .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                .where(productPost.user.eq(user))
                .orderBy(productPost.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        String imageLink;
        for (MyPosting myPosting : response) {
            imageLink = myPosting.getImageLink().split(",")[0];
            myPosting.setImageLink(imageLink);
        }
        System.out.println("response = " + response);
        return response;
    }

    @Override
    public void refreshUpdateAt(long productPostId) {
        queryFactory
                .update(productPost)
                .set(productPost.updatedAt, LocalDateTime.now())
                .where(productPost.id.eq(productPostId))
                .execute();
    }

    @Override
    public void convertStatus(long productPostId) {
        queryFactory
                .update(productPost)
                .set(productPost.status, false)
                .where(productPost.id.eq(productPostId))
                .execute();
    }
}
