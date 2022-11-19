package com.usw.sugo.domain.note.note.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteRoomForm;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
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
    public List<List<LoadNoteListForm>> loadNoteListByUserId(long requestUserId, Pageable pageable) {

        List<List<LoadNoteListForm>> finalResult = new ArrayList<>();
        // 요청한 유저가 만든 쪽지방 리스트
        List<LoadNoteListForm> loadNoteListResultByNoteCreatingUser =
                queryFactory
                        .select(Projections.bean(LoadNoteListForm.class,
                                note.id.as("roomId"),
                                note.creatingUserId.id.as("requestUserId"),
                                note.opponentUserId.id.as("opponentUserId"),
                                note.opponentUserId.nickname.as("opponentUserNickname"),
                                note.recentContent,
                                note.creatingUserUnreadCount.as("requestUserUnreadCount"),
                                note.updatedAt.as("recentChattingDate")
                        ))
                        .from(note)
                        .where(note.creatingUserId.id.eq(requestUserId))
                        .orderBy(note.updatedAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        // 요청한 유저가 만들진 않았지만, 개설된 쪽지방
        List<LoadNoteListForm> loadNoteListResultByNoteCreatedUser =
                queryFactory
                        .select(Projections.bean(LoadNoteListForm.class,
                                note.id.as("roomId"),
                                note.opponentUserId.id.as("requestUserId"),
                                note.creatingUserId.id.as("opponentUserId"),
                                note.creatingUserId.nickname.as("opponentUserNickname"),
                                note.recentContent,
                                note.opponentUserUnreadCount.as("requestUserUnreadCount"),
                                note.updatedAt.as("recentChattingDate")
                        ))
                        .from(note)
                        .where(note.opponentUserId.id.eq(requestUserId))
                        .orderBy(note.updatedAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        finalResult.add(loadNoteListResultByNoteCreatingUser);
        finalResult.add(loadNoteListResultByNoteCreatedUser);

        return finalResult;
    }

    /*
    특정 쪽지방에 입장, 채팅 메세지 반환
     */
    @Override
    public List<LoadNoteRoomForm> loadNoteRoomAllContentByRoomId(long requestUserId, long roomId, Pageable pageable) {

        queryFactory
                .update(note)
                .set(note.creatingUserUnreadCount, 0)
                .where(note.creatingUserId.id.eq(requestUserId)
                        .and(note.id.eq(roomId)))
                .execute();

        queryFactory
                .update(note)
                .set(note.opponentUserUnreadCount, 0)
                .where(note.opponentUserId.id.eq(requestUserId)
                        .and(note.id.eq(roomId)))
                .execute();

        return queryFactory
                .select(Projections.bean(LoadNoteRoomForm.class,
                        noteContent.message,
                        noteContent.sender.id.as("messageSenderId"),
                        noteContent.receiver.id.as("messageReceiverId"),
                        noteContent.createdAt.as("messageCreatedAt"),
                        noteFile.imageLink,
                        noteFile.sender.id.as("fileSenderId"),
                        noteFile.receiver.id.as("fileReceiverId"),
                        noteFile.createdAt.as("fileCreatedAt")
                ))
                .from(noteContent, noteFile)
                .where(noteFile.noteId.id.eq(roomId)
                        .or(noteContent.noteId.id.eq(roomId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(noteContent.createdAt.desc(), noteFile.createdAt.desc())
                .fetch();
    }

    // Note 테이블에 최근 메세지 반영
    @Override
    public void updateRecentContent(long unreadUserId, long roomId, String content, String imageLink) {

        // 파일을 보냈을 때
        if (!content.equals("")) {
            queryFactory
                    .update(note)
                    .set(note.recentContent, content)
                    .set(note.updatedAt, LocalDateTime.now())
                    .where(note.id.eq(roomId))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.opponentUserUnreadCount, note.opponentUserUnreadCount.add(1))
                    .where(note.id.eq(roomId)
                            .and(note.opponentUserId.id.eq(unreadUserId)))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.creatingUserUnreadCount, note.creatingUserUnreadCount.add(1))
                    .where(note.id.eq(roomId)
                            .and(note.creatingUserId.id.eq(unreadUserId)))
                    .execute();
            return;
        }

        queryFactory
                .update(note)
                .set(note.recentContent, imageLink)
                .set(note.updatedAt, LocalDateTime.now())
                .where(note.id.eq(roomId))
                .execute();
        queryFactory
                .update(note)
                .set(note.opponentUserUnreadCount, note.opponentUserUnreadCount.add(1))
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


    @Override
    public void findNoteByRequestUserAndTargetUserAndProductPost(long noteRequestUserId, long targetUserId, long productPostId) {
        List<Note> fetch = queryFactory
                .selectFrom(note)
                .where(note.creatingUserId.id.eq(noteRequestUserId)
                        .and(note.opponentUserId.id.eq(targetUserId))
                        .and(note.productPost.id.eq(productPostId)))
                .fetch();

        if (fetch.size() != 0) {
            throw new CustomException(ErrorCode.NOTE_ALREADY_CREATED);
        }
    }
}