package com.usw.sugo.domain.productpost.productpost.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PutContentRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.dto.QPostResponseDto_DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.dto.QPostResponseDto_MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.dto.QPostResponseDto_SearchResultResponse;
import com.usw.sugo.domain.productpost.productpost.service.CategoryValidator;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.dto.QUserResponseDto_MyPosting;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.MyPosting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.usw.sugo.domain.productpost.entity.QProductPost.productPost;
import static com.usw.sugo.domain.productpost.entity.QProductPostFile.productPostFile;
import static com.usw.sugo.domain.productpost.entity.QUserLikePost.userLikePost;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostRepositoryImpl implements CustomProductPostRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * @param value
     * @param inputCategory
     * @return 검색결과 조회
     * 수정날짜 내림차순
     */
    @Override
    public List<SearchResultResponse> searchPost(String value, String inputCategory) {
        List<SearchResultResponse> response = new ArrayList<>();
        if (inputCategory.equals("")) {
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
        } else if (CategoryValidator.validateCategory(inputCategory)) {
            response = queryFactory
                    .select(new QPostResponseDto_SearchResultResponse(
                            productPost.id,
                            productPostFile.imageLink,
                            productPost.contactPlace, productPost.updatedAt,
                            productPost.title, productPost.price,
                            productPost.user.nickname, productPost.category, productPost.status))
                    .from(productPost)
                    .where(productPost.title.contains(value)
                            .and(productPost.category.eq(inputCategory)))
                    .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                    .orderBy(productPost.updatedAt.desc())
                    .fetch();
        }

        // Comma 로 구분되어있는 이미지 링크 List 로 캐스팅 시작
        int listSize = response.size();
        String[] imageList;

        // 쿼리로 불러온 게시글 갯수만큼 반복한다.
        for (int i = 0; i < listSize; i++) {
            // 각 게시글마다 이미지 링크값을 검사하고, 비어있으면 빈 문자열로 반환하도록 한다.
            if (response.get(i).getImageLink() == null) {
                response.get(i).setImageLink("");
            }
            // 각 게시글마다 이미지 링크값을 검사하고, 값이 있으면 그 값을 ,로 구분하여 반환한다.
            else if (response.get(i).getImageLink() != null) {
                imageList = response.get(i).getImageLink().split(",");
                response.get(i).setImageLink(Arrays.toString(imageList));
            }
        }
        // Comma 로 구분되어있는 이미지 링크 List 로 캐스팅 종료

        return response;
    }

    /**
     * @param pageable
     * @param inputCategory
     * @return 메인페이지 조회
     * 수정날짜 내림차순으로, 10개를 뽑아온다. (페이징)
     */
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
    public void editPostContent(PutContentRequest putContentRequest) {
        queryFactory
                .update(productPost)
                .set(productPost.title, putContentRequest.getTitle())
                .set(productPost.content, putContentRequest.getContent())
                .set(productPost.price, putContentRequest.getPrice())
                .set(productPost.contactPlace, putContentRequest.getContactPlace())
                .set(productPost.category, putContentRequest.getCategory())
                .where(productPost.id.eq(putContentRequest.getProductPostId()))
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
