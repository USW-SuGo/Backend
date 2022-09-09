package com.usw.sugo.domain.majorproduct.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponseForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.usw.sugo.domain.majorproduct.QProductPost.productPost;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostRepositoryImpl implements CustomProductPostRepository {

    private final JPAQueryFactory queryFactory;

    // 메인페이지 반환 메서드
    // Projections.constructor(변환할 DTO, 조회할 컬럼1, 조회할 컬럼2, 조회할 컬럼3, ...) --> Tuple to DTO
    // 수정날짜 내림차순으로, 10개를 뽑아온다.

    @Override
    public List<MainPageResponseForm> loadMainPagePostList() {
        return queryFactory
                .select(Projections.constructor(MainPageResponseForm.class,
                        productPost.title, productPost.contactPlace, productPost.productPostFile.imageLink))
                .from(productPost)
                .orderBy(productPost.updatedAt.desc())
                .limit(10)
                .fetch();
    }
}
