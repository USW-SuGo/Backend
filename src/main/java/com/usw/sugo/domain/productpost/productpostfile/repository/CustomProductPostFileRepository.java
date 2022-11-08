package com.usw.sugo.domain.productpost.productpostfile.repository;

import com.usw.sugo.domain.productpost.productpost.dto.PostRequestDto;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomProductPostFileRepository {

    void editPostFile(String imageLinkStringBuilder, PostRequestDto.PutContentRequest putContentRequest);

}
