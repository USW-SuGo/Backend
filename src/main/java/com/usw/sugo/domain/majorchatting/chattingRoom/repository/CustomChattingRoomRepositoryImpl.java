package com.usw.sugo.domain.majorchatting.chattingRoom.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majoruser.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.domain.majorchatting.QChattingRoom.chattingRoom;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomChattingRoomRepositoryImpl implements CustomChattingRoomRepository {

    private final JPAQueryFactory queryFactory;

    /*
     채팅목록 불러오기
     판매자/구매자로 등록되어있는 모든 채팅방을 가져온다.
     */
    @Override
    public List<ChattingRoom> findAllRoomByRequestUserId(User user) {
        return queryFactory
                .select(chattingRoom)
                .from(chattingRoom)
                .where(chattingRoom.sender.eq(user).or(chattingRoom.receiver.eq(user)))
                .fetch();
    }

    // 1주일 동안 채팅이 이루어지지 않으면 자동 삭제
    @Override
    public void deleteBeforeWeek() {
        queryFactory
                .delete(chattingRoom)
                .where(chattingRoom.updatedAt.before(LocalDateTime.now().minusWeeks(1)));
    }

    @Override
    public void testDeleteBeforeWeek() {
        queryFactory
                .delete(chattingRoom)
                .where(chattingRoom.updatedAt.before(LocalDateTime.now()))
                .execute();
    }
}
