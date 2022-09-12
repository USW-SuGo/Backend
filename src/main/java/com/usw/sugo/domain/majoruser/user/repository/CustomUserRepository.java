package com.usw.sugo.domain.majoruser.user.repository;

import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    void authorizeToken(Long id);

    void detailJoin(DetailJoinRequest detailJoinRequest, Long userId);

    void editPassword(Long id, String password);

    void editNickname(Long id, String nickName);

    void setModifiedDate(Long id);
}
