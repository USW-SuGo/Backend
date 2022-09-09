package com.usw.sugo.domain.majorproduct.repository;

import com.querydsl.core.Tuple;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CustomProductPostRepository {

    List<PostResponseDto.MainPageResponseForm> loadMainPagePostList();
}
