package com.usw.sugo.domain.majornote.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomResponseDto;
import com.usw.sugo.domain.majornote.QNoteContent;
import com.usw.sugo.domain.majornote.QNoteFile;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteFileForm;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteForm;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteMessageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.domain.majorchatting.QChattingRoom.chattingRoom;
import static com.usw.sugo.domain.majorchatting.QChattingRoomFile.chattingRoomFile;
import static com.usw.sugo.domain.majorchatting.QChattingRoomMessage.chattingRoomMessage;
import static com.usw.sugo.domain.majornote.QNote.note;
import static com.usw.sugo.domain.majornote.QNoteContent.noteContent;
import static com.usw.sugo.domain.majornote.QNoteFile.noteFile;
import static com.usw.sugo.domain.majoruser.QUser.user;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomNoteRepositoryImpl implements CustomNoteRepository {

    private final JPAQueryFactory queryFactory;


    // 한달 동안 쪽지가 이루어지지 않으면 자동 삭제
    @Override
    public void deleteBeforeWeek() {
        queryFactory
                .delete(note)
                .where(note.updatedAt.before(LocalDateTime.now().minusMonths(1)))
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
    public List<LoadNoteListForm> loadChattingRoomListByUserId(long userId, Pageable pageable) {
        return
                queryFactory
                        .select(Projections.bean(LoadNoteListForm.class,
                                note.id.as("roomId"),
                                note.sellerId.id.as("sellerId"),
                                note.buyerId.id.as("buyerId"),
                                note.sellerId.nickname.as("sellerNickname"),
                                note.buyerId.nickname.as("buyerNickname"),
                                note.updatedAt.as("recentChattingDate")
                        ))
                        .from(note)
                        .where(note.sellerId.id.eq(userId).or(note.buyerId.id.eq(userId)))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
    }

    /*
    특정 채팅방에 존재하는 사용자 및 상품 데이터 반환
     */
    @Override
    public List<LoadNoteForm> loadChattingRoomFormByRoomId(long roomId) {
        return queryFactory
                .select(Projections.bean(LoadNoteForm.class,
                        note.id.as("roomId"),
                        note.sellerId.id.as("sellerId"),
                        note.buyerId.id.as("buyerId"),
                        note.sellerId.nickname.as("sellerNickname"),
                        note.buyerId.nickname.as("buyerNickname"),
                        note.productPost.title, note.productPost.contactPlace,
                        note.productPost.price))
                .from(note)
                .where(note.id.eq(roomId))
                .fetch();
    }

    /*
    특정 채팅방에 존재하는 채팅 메세지, 파일 반환
    (채팅 내역 추가 해야함)
     */
    @Override
    public List<LoadNoteMessageForm> loadChattingRoomMessageFormByRoomId(long roomId, Pageable pageable) {
        return queryFactory
                .select(Projections.bean(LoadNoteMessageForm.class,
                        noteContent.sender.id.as("senderId"),
                        noteContent.receiver.id.as("receiverId"),
                        noteContent.message, noteContent.createdAt
                ))
                .from(noteContent)
                .where(noteContent.noteId.eq(
                        JPAExpressions
                                .select(note)
                                .from(note)
                                .where(note.id.eq(roomId))
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(noteContent.createdAt.desc())
                .fetch();
    }

    /*
    특정 채팅방에 존재하는 채팅 메세지, 파일 반환
    (채팅 내역 추가 해야함)
     */
    @Override
    public List<LoadNoteFileForm> loadChattingRoomFileFormByRoomId(long roomId, Pageable pageable) {
        return queryFactory
                .select(Projections.bean(LoadNoteFileForm.class,
                        noteFile.sender.id.as("senderId"),
                        noteFile.receiver.id.as("receiverId"),
                        noteFile.imageLink, noteFile.createdAt
                ))
                .from(noteFile)
                .where(noteFile.noteId.eq(
                        JPAExpressions
                                .select(note)
                                .from(note)
                                .where(note.id.eq(roomId))
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(noteFile.createdAt.desc())
                .fetch();
    }
}