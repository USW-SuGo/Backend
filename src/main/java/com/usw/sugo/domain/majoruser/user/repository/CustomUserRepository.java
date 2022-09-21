package com.usw.sugo.domain.majoruser.user.repository;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    void modifyingStatusToAvailable(Long id);

    void detailJoin(DetailJoinRequest detailJoinRequest, Long userId);

    void editPassword(Long id, String password);

    void setModifiedDate(Long id);

    void findNicknameNumber(String department);

    User findByEmailForUserDetails(String email);

}
