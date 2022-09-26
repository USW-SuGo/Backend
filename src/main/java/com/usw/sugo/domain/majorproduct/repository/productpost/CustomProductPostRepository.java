package com.usw.sugo.domain.majorproduct.repository.productpost;

import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PostingContentRequest;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.MyPosting;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CustomProductPostRepository {

    List<MainPageResponse> loadMainPagePostList(Pageable pageable);

    DetailPostResponse loadDetailPostList(Long productPostId);

    List<MyPosting> loadUserPageList(User user, Pageable pageable);

    void refreshUpdateAt(Long productPostId);

    void editPostContent(StringBuilder imageLinkStringBuilder, Long productPostId, PostingContentRequest postingContentRequest);
}
