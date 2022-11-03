package com.usw.sugo.domain.majorproduct.repository.productpost;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.MyPosting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.usw.sugo.domain.majorproduct.QProductPost.productPost;
import static com.usw.sugo.domain.majorproduct.QProductPostFile.productPostFile;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostRepositoryImpl implements CustomProductPostRepository {

    private final JPAQueryFactory queryFactory;

    /**
     *
     * @param pageable
     * @param searchValue
     * @return 검색결과 조회
     * 수정날짜 내림차순으로, 10개를 뽑아온다. (페이징)
     */
    @Override
    public List<SearchResultResponse> searchPost(Pageable pageable, String searchValue, String category) {

        List<SearchResultResponse> response = queryFactory
                    .select(Projections.bean(SearchResultResponse.class,
                            productPost.id,
                            productPostFile.imageLink,
                            productPost.contactPlace, productPost.updatedAt,
                            productPost.title, productPost.price,
                            productPost.user.nickname, productPost.category))
                    .from(productPost)
                    .where(productPost.title.contains(searchValue).and(productPost.category.eq(category)))
                    .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                    .orderBy(productPost.updatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

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
     *
     * @param pageable
     * @param category
     * @return 메인페이지 조회
     *         수정날짜 내림차순으로, 10개를 뽑아온다. (페이징)
     */
    @Override
    public List<MainPageResponse> loadMainPagePostList(Pageable pageable, String category) {

        List<MainPageResponse> response = new ArrayList<>();

        if (category.equals("")) {
            response = queryFactory
                    .select(Projections.bean(MainPageResponse.class,
                            productPost.id,
                            productPostFile.imageLink,
                            productPost.contactPlace, productPost.updatedAt,
                            productPost.title, productPost.price,
                            productPost.user.nickname, productPost.category))
                    .from(productPost)
                    .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                    .orderBy(productPost.updatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }
        else if (!category.equals("")) {
            response = queryFactory
                    .select(Projections.bean(MainPageResponse.class,
                            productPost.id,
                            productPostFile.imageLink,
                            productPost.contactPlace, productPost.updatedAt,
                            productPost.title, productPost.price,
                            productPost.user.nickname, productPost.category))
                    .from(productPost)
                    .where(productPost.category.eq(category))
                    .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                    .orderBy(productPost.updatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
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

    /*
    특정 게시물 조회
     */
    @Override
    public DetailPostResponse loadDetailPostList(long productPostId) {

        DetailPostResponse response = queryFactory
                .select(Projections.bean(DetailPostResponse.class,
                        productPost.id,
                        productPostFile.imageLink,
                        productPost.contactPlace, productPost.updatedAt,
                        productPost.title, productPost.content, productPost.price,
                        productPost.user.nickname, productPost.category))
                .from(productPost)
                .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                .where(productPost.id.eq(productPostId))
                .fetchOne();

        // Comma 로 구분되어있는 이미지 링크 List 로 캐스팅 시작
        String[] imageList;
        imageList = response.getImageLink().split(",");
        response.setImageLink(Arrays.toString(imageList));
        // Comma 로 구분되어있는 이미지 링크 List 로 캐스팅 종료

        return response;
    }

    // 유저 페이지 조회
    @Override
    public List<MyPosting> loadUserPageList(User user, Pageable pageable) {

        List<MyPosting> response = queryFactory
                .select(Projections.bean(MyPosting.class,
                        productPost.id,
                        productPostFile.imageLink,
                        productPost.contactPlace, productPost.updatedAt,
                        productPost.title, productPost.price, productPost.category))
                .from(productPost)
                .leftJoin(productPostFile).on(productPostFile.productPost.id.eq(productPost.id))
                .where(productPost.user.eq(user))
                .orderBy(productPost.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int listSize = response.size();

        String[] imageList;

        for (int i = 0; i < listSize; i++) {
            imageList = response.get(i).getImageLink().split(",");
            response.get(i).setImageLink(Arrays.toString(imageList));
        }

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
    public void editPostContent(StringBuilder imageLinkStringBuilder,
                                PostingRequest postingRequest) {
        queryFactory
                .update(productPost)
                .set(productPost.title, postingRequest.getTitle())
                .set(productPost.content, postingRequest.getContent())
                .set(productPost.price, postingRequest.getPrice())
                .set(productPost.contactPlace, postingRequest.getContactPlace())
                .set(productPost.category, postingRequest.getCategory())
                .where(productPost.id.eq(postingRequest.getProductPostId()))
                .execute();

        queryFactory
                .update(productPostFile)
                .set(productPostFile.imageLink, imageLinkStringBuilder.toString())
                .where(productPostFile.productPost.id.eq(postingRequest.getProductPostId()))
                .execute();
    }

    @Override
    public void convertStatus(long productPostId) {
        queryFactory
                .update(productPost)
                .set(productPost.status, true)
                .where(productPost.id.eq(productPostId))
                .execute();
    }
}
