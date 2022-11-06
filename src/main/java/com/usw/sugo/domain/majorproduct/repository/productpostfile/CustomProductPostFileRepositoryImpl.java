package com.usw.sugo.domain.majorproduct.repository.productpostfile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PutContentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import static com.usw.sugo.domain.majorproduct.QProductPostFile.productPostFile;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostFileRepositoryImpl implements CustomProductPostFileRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void editPostFile(
            String updatedImageLink, PutContentRequest putContentRequest) {
        queryFactory
                .update(productPostFile)
                .set(productPostFile.imageLink, updatedImageLink)
                .where(productPostFile.productPost.id.eq(putContentRequest.getProductPostId()))
                .execute();
    }
}
