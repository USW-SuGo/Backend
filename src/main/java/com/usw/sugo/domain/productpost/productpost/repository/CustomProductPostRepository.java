package com.usw.sugo.domain.productpost.productpost.repository;

import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto.PutContentRequest;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.dto.PostResponseDto.SearchResultResponse;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.MyPosting;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomProductPostRepository {

    List<SearchResultResponse> searchPost(String searchValue, String category);

    List<MainPageResponse> loadMainPagePostList(Pageable pageable, String category);

    DetailPostResponse loadDetailPostList(long productPostId, long userId);

    List<MyPosting> loadUserWritingPostingList(User user, Pageable pageable);

    void refreshUpdateAt(long productPostId);

    void editPostContent(PutContentRequest putContentRequest);

    void convertStatus(long productPostId);
}
