package com.usw.sugo.domain.productpost.productpostfile.repository;

import static com.usw.sugo.domain.productpost.productpostfile.QProductPostFile.productPostFile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostFileRepositoryImpl implements CustomProductPostFileRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByProductPost(ProductPost productPost) {
        queryFactory
            .delete(productPostFile)
            .where(productPostFile.productPost.id.eq(productPost.getId()))
            .execute();
    }
}
