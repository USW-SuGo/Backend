package com.usw.sugo.domain.productpostfile.repository;

import com.usw.sugo.domain.productpost.dto.PostRequestDto;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomProductPostFileRepository {

    void editPostFile(String imageLinkStringBuilder, PostRequestDto.PutContentRequest putContentRequest);

}
