package com.usw.sugo.domain.majorproduct.repository.productpost;

import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PostRequest;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CustomProductPostRepository {

    List<MainPageResponse> loadMainPagePostList(Pageable pageable);
}
