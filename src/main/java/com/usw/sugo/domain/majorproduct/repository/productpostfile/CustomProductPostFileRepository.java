package com.usw.sugo.domain.majorproduct.repository.productpostfile;

import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CustomProductPostFileRepository {

    List<MainPageResponse> loadMainPagePostList(Pageable pageable);
}
