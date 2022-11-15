package com.usw.sugo.domain.chatting.room.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingListForm;
import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingRoomFileForm;
import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingRoomForm;
import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingRoomMessageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.domain.chatting.entity.QChattingRoom.chattingRoom;
import static com.usw.sugo.domain.chatting.entity.QChattingRoomFile.chattingRoomFile;
import static com.usw.sugo.domain.chatting.entity.QChattingRoomMessage.chattingRoomMessage;
import static com.usw.sugo.domain.user.entity.QUser.user;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomChattingRoomRepositoryImpl implements CustomChattingRoomRepository {

    private final JPAQueryFactory queryFactory;


    // 1주일 동안 채팅이 이루어지지 않으면 자동 삭제
    @Override
    public void deleteBeforeWeek() {
        queryFactory
                .delete(chattingRoom)
                .where(chattingRoom.updatedAt.before(LocalDateTime.now().minusWeeks(1)))
                .execute();
    }

    /*
    채팅방 목록 불러오기 (가장 최근 채팅이 무엇인지 내려주는게 엄청 어려움. + 가장 최근 보낸 시각을 뽑아오기도 어려워함)

    --> 쿼리로 해결한 것이 아니라, 테이블에 최근 메세징 컬럼을 추가하여 가장 최근에 추가한 내용을 조회할 수 있도록함
    --> 매 메세지를 보낼 때 마다 해당 테이블의 컬럼값을 수정해야한다는 단점이 있다.
     --> 하지만 채팅방 목록을 불로올 때마다 수행하게 될
     --> 여러번의 조인보다 컬럼 하나를 추가하는게 더 좋을 수도 있겠다는생각으로 도입하였다.
     */
    @Override
    public List<LoadChattingListForm> loadChattingRoomListByUserId(long userId, Pageable pageable) {
        return
                queryFactory
                        .select(Projections.bean(LoadChattingListForm.class,
                                chattingRoom.id.as("roomId"),
                                chattingRoom.sellerId.id.as("sellerId"),
                                chattingRoom.buyerId.id.as("buyerId"),
                                chattingRoom.sellerId.nickname.as("sellerNickname"),
                                chattingRoom.buyerId.nickname.as("buyerNickname"),
                                chattingRoom.updatedAt.as("recentChattingDate")
                        ))
                        .from(chattingRoom)
                        .where(chattingRoom.sellerId.id.eq(userId).or(chattingRoom.buyerId.id.eq(userId)))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
    }

    /*
    특정 채팅방에 존재하는 사용자 및 상품 데이터 반환
     */
    @Override
    public List<LoadChattingRoomForm> loadChattingRoomFormByRoomId(long roomId) {
        return queryFactory
                .select(Projections.bean(LoadChattingRoomForm.class,
                        chattingRoom.id.as("roomId"),
                        chattingRoom.sellerId.id.as("sellerId"),
                        chattingRoom.buyerId.id.as("buyerId"),
                        chattingRoom.sellerId.nickname.as("sellerNickname"),
                        chattingRoom.buyerId.nickname.as("buyerNickname"),
                        chattingRoom.productPost.title, chattingRoom.productPost.contactPlace,
                        chattingRoom.productPost.price))
                .from(chattingRoom)
                .where(chattingRoom.id.eq(roomId))
                .fetch();
    }

    /*
    특정 채팅방에 존재하는 채팅 메세지, 파일 반환
    (채팅 내역 추가 해야함)
     */
    @Override
    public List<LoadChattingRoomMessageForm> loadChattingRoomMessageFormByRoomId(long roomId, Pageable pageable) {
        return queryFactory
                .select(Projections.bean(LoadChattingRoomMessageForm.class,
                        chattingRoomMessage.sender.id.as("senderId"),
                        chattingRoomMessage.receiver.id.as("receiverId"),
                        chattingRoomMessage.message, chattingRoomMessage.createdAt
                        ))
                .from(chattingRoomMessage)
                .where(chattingRoomMessage.chattingRoomId.eq(
                        JPAExpressions
                                .select(chattingRoom)
                                .from(chattingRoom)
                                .where(chattingRoom.id.eq(roomId))
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(chattingRoomMessage.createdAt.desc())
                .fetch();
    }

    /*
    특정 채팅방에 존재하는 채팅 메세지, 파일 반환
    (채팅 내역 추가 해야함)
     */
    @Override
    public List<LoadChattingRoomFileForm> loadChattingRoomFileFormByRoomId(long roomId, Pageable pageable) {
        return queryFactory
                .select(Projections.bean(LoadChattingRoomFileForm.class,
                        ExpressionUtils.as(JPAExpressions.
                                select(user.id)
                                .from(user)
                                .where(user.id.eq(chattingRoomFile.sender.id)), "senderId"),
                        ExpressionUtils.as(JPAExpressions.
                                select(user.id)
                                .from(user)
                                .where(user.id.eq(chattingRoomFile.receiver.id)), "receiverId"),
                        chattingRoomFile.imageLink, chattingRoomFile.createdAt
                ))
                .from(chattingRoomFile)
                .where(chattingRoomFile.chattingRoomId.eq(
                        JPAExpressions
                                .select(chattingRoom)
                                .from(chattingRoom)
                                .where(chattingRoom.id.eq(roomId))
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(chattingRoomFile.createdAt.desc())
                .fetch();
    }
}