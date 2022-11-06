package com.usw.sugo.domain.majorproduct.repository.productpostfile;

import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PutContentRequest;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomProductPostFileRepository {

    void editPostFile(String imageLinkStringBuilder, PutContentRequest putContentRequest);

}
