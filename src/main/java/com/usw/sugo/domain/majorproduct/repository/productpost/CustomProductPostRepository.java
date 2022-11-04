package com.usw.sugo.domain.majorproduct.repository.productpost;

import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PutContentRequest;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.MyPosting;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomProductPostRepository {

    List<SearchResultResponse> searchPost(String searchValue, String category);

    List<MainPageResponse> loadMainPagePostList(Pageable pageable, String category);

    DetailPostResponse loadDetailPostList(long productPostId);

    List<MyPosting> loadUserPageList(User user, Pageable pageable);

    void refreshUpdateAt(long productPostId);

    void editPostContent(StringBuilder imageLinkStringBuilder, PutContentRequest putContentRequest);

    void convertStatus(long productPostId);
}
