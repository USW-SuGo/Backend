package com.usw.sugo.domain.majorproduct.repository.productpost;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.MyPosting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.usw.sugo.domain.majorproduct.QProductPost.productPost;
import static com.usw.sugo.domain.majorproduct.QProductPostFile.productPostFile;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostRepositoryImpl implements CustomProductPostRepository {

    private final JPAQueryFactory queryFactory;

    // 메인페이지 반환 메서드
    // 수정날짜 내림차순으로, 10개를 뽑아온다. (페이징)
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

        // null -> 빈 문자열로 치환
        for (MainPageResponse mainPageResponse : response) {
            if (mainPageResponse.getImageLink() == null) {
                mainPageResponse.setImageLink("");
            }
        }
        return response;
    }

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
                .orderBy(productPost.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return response;
    }


}
