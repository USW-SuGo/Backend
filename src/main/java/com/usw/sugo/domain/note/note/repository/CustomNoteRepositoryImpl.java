package com.usw.sugo.domain.note.note.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteFileForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListCreatingByOpponentUserForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListCreatingByRequestUserForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteMessageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.usw.sugo.domain.note.entity.QNote.note;
import static com.usw.sugo.domain.note.entity.QNoteContent.noteContent;
import static com.usw.sugo.domain.note.entity.QNoteFile.noteFile;

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
    public List<Object> loadNoteListByUserId(
            long requestUserId, long opponentUserId, Pageable pageable) {

        // 요청한 유저가 만든 쪽지방 리스트
        List<LoadNoteListCreatingByRequestUserForm> creatingUserFormList = queryFactory
                .select(Projections.bean(LoadNoteListCreatingByRequestUserForm.class,
                        note.id.as("roomId"),
                        note.opponentUserId.id.as("opponentUserId"),
                        note.opponentUserId.nickname.as("opponentUserNickname"),
                        note.recentContent, note.creatingUserUnreadCount,
                        note.updatedAt.as("recentChattingDate")
                ))
                .from(note)
                .where(note.creatingUserId.id.eq(requestUserId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 다른 유저가 만든 쪽지방 리스트에 요청한 유저가 속한경우
        List<LoadNoteListCreatingByOpponentUserForm> opponentUserFormList = queryFactory
                .select(Projections.bean(LoadNoteListCreatingByOpponentUserForm.class,
                        note.id.as("roomId"),
                        note.creatingUserId.id.as("creatingUserId"),
                        note.creatingUserId.nickname.as("creatingUserNickname"),
                        note.recentContent, note.opponentUserUnreadCount,
                        note.updatedAt.as("recentChattingDate")
                ))
                .from(note)
                .where(note.creatingUserId.id.eq(requestUserId).not()
                        .and(note.opponentUserId.id.eq(requestUserId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new ArrayList<>() {{
            add(creatingUserFormList);
            add(opponentUserFormList);
        }};
    }

    /*
    특정 쪽지방에 입장, 채팅 메세지 반환
     */
    @Override
    public List<LoadNoteMessageForm> loadNoteMessageFormByRoomId(long roomId, Pageable pageable) {
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
    특정 쪽지방에 입장, 존재하는 파일 반환
     */
    @Override
    public List<LoadNoteFileForm> loadNoteFileFormByRoomId(long roomId, Pageable pageable) {

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

    @Override
    public void updateRecentContent(long unreadUserId, long roomId, String content, String imageLink) {

        if (!content.equals("")) {
            queryFactory
                    .update(note)
                    .set(note.recentContent, content)
                    .set(note.updatedAt, LocalDateTime.now())
                    .where(note.id.eq(roomId))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.creatingUserUnreadCount, note.creatingUserUnreadCount.add(1))
                    .where(note.id.eq(roomId)
                            .and(note.opponentUserId.id.eq(unreadUserId)))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.creatingUserUnreadCount, note.creatingUserUnreadCount.add(1))
                    .where(note.id.eq(roomId)
                            .and(note.creatingUserId.id.eq(unreadUserId)))
                    .execute();
        } else {
            queryFactory
                    .update(note)
                    .set(note.recentContent, imageLink)
                    .set(note.updatedAt, LocalDateTime.now())
                    .where(note.id.eq(roomId))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.creatingUserUnreadCount, note.creatingUserUnreadCount.add(1))
                    .where(note.id.eq(roomId)
                            .and(note.opponentUserId.id.eq(unreadUserId)))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.creatingUserUnreadCount, note.creatingUserUnreadCount.add(1))
                    .where(note.id.eq(roomId)
                            .and(note.creatingUserId.id.eq(unreadUserId)))
                    .execute();
        }
    }
}