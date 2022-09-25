package com.usw.sugo.domain.majoruser.user.repository;

import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinRequest;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CustomUserRepository {

    void modifyingStatusToAvailable(Long id);

    void detailJoin(DetailJoinRequest detailJoinRequest, Long userId);

    void editPassword(Long id, String password);

    void setModifiedDate(Long id);

    void findNicknameNumber(String department);

    void setMannerGrade(BigDecimal grade, long targetUserId, long evaluatingUserId);

}
