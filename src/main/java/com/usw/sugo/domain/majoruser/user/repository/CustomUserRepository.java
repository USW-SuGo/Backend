package com.usw.sugo.domain.majoruser.user.repository;

import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinFormRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    void authorizeToken(Long id);

    void detailJoin(DetailJoinFormRequest detailJoinFormRequest);
}
