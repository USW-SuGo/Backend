package com.usw.sugo.domain.majorchatting.chattingRoom.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majoruser.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
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

    /*
    메세지 보내기
    DB 에 파라미터로 들어온 메세지 전송자/수신자/메세지를 저장
     */
}
