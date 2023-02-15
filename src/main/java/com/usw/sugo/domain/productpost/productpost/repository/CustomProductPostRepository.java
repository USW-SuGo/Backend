package com.usw.sugo.domain.productpost.productpost.repository;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.ClosePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MyPosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomProductPostRepository {

    // Spring Data JPA delete()를 사용하면 SELECT 쿼리가 한 번 더 나가는 것을 방지하기 위함
    void deleteByEntity(ProductPost requestProductPost);

    List<SearchResultResponse> searchPost(String searchValue, String category);

    List<MainPageResponse> loadMainPagePostList(Pageable pageable, String category);

    DetailPostResponse loadDetailPost(Long productPostId, Long userId);

    List<MyPosting> loadWrittenPost(User user, Pageable pageable);

    List<ClosePosting> loadClosePost(User user, Pageable pageable);
}
