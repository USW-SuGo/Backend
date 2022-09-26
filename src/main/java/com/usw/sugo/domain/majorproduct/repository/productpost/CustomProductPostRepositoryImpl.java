package com.usw.sugo.domain.majorproduct.repository.productpost;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PostingContentRequest;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.MyPosting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.usw.sugo.domain.majorproduct.QProductPost.productPost;
import static com.usw.sugo.domain.majorproduct.QProductPostFile.productPostFile;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostRepositoryImpl implements CustomProductPostRepository {

    private final JPAQueryFactory queryFactory;

    /*
    메인페이지 조회
    수정날짜 내림차순으로, 10개를 뽑아온다. (페이징)
     */
    @Override
    public List<MainPageResponse> loadMainPagePostList(Pageable pageable) {

        List<MainPageResponse> response = queryFactory
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

        // Comma 로 구분되어있는 이미지 링크 List 로 캐스팅 시작
        int listSize = response.size();
        String[] imageList;
        for (int i = 0; i < listSize; i++) {
            imageList = response.get(i).getImageLink().split(",");
            response.get(i).setImageLink(Arrays.toString(imageList));
        }
        // Comma 로 구분되어있는 이미지 링크 List 로 캐스팅 종료

        return response;
    }

    /*
    특정 게시물 조회
     */
    @Override
    public DetailPostResponse loadDetailPostList(Long productPostId) {

        DetailPostResponse response = queryFactory
                .select(Projections.bean(DetailPostResponse.class,
                        productPost.id,
                        productPostFile.imageLink,
                        productPost.contactPlace, productPost.updatedAt,
                        productPost.title, productPost.price,
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
    public void refreshUpdateAt(Long productPostId) {
        queryFactory
                .update(productPost)
                .set(productPost.updatedAt, LocalDateTime.now())
                .where(productPost.id.eq(productPostId))
                .execute();
    }

    @Override
    public void editPostContent(StringBuilder imageLinkStringBuilder,
                                Long productPostId,
                                PostingContentRequest postingContentRequest) {
        queryFactory
                .update(productPost)
                .set(productPost.title, postingContentRequest.getTitle())
                .set(productPost.content, postingContentRequest.getContent())
                .set(productPost.price, postingContentRequest.getPrice())
                .set(productPost.contactPlace, postingContentRequest.getContactPlace())
                .set(productPost.category, postingContentRequest.getCategory())
                .where(productPost.id.eq(productPostId))
                .execute();

        queryFactory
                .update(productPostFile)
                .set(productPostFile.imageLink, imageLinkStringBuilder.toString())
                .where(productPostFile.productPost.id.eq(productPostId))
                .execute();
    }
}
