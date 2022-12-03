package com.usw.sugo.domain.note.note.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.dto.QNoteResponseDto_LoadNoteListForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.usw.sugo.domain.note.entity.QNote.note;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomNoteRepositoryImpl implements CustomNoteRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public void deleteBeforeWeek() {
        queryFactory
                .delete(note)
                .where(note.updatedAt.before(LocalDateTime.now().minusMonths(1)))
                .execute();
    }

    @Override
    public List<List<LoadNoteListForm>> loadNoteListByUserId(long requestUserId, Pageable pageable) {
        List<List<LoadNoteListForm>> finalResult = new ArrayList<>();
        List<LoadNoteListForm> loadNoteListResultByNoteCreatingUser =
                queryFactory
                        .select(new QNoteResponseDto_LoadNoteListForm(
                                note.id, note.productPost.id, note.creatingUser.id, note.opponentUser.id,
                                note.opponentUserNickname, note.recentContent, note.creatingUserUnreadCount,
                                note.updatedAt))
                        .from(note)
                        .where(note.creatingUser.id.eq(requestUserId))
                        .orderBy(note.updatedAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        List<LoadNoteListForm> loadNoteListResultByNoteCreatedUser =
                queryFactory
                        .select(new QNoteResponseDto_LoadNoteListForm(
                                note.id, note.productPost.id, note.creatingUser.id, note.opponentUser.id,
                                note.opponentUserNickname, note.recentContent, note.creatingUserUnreadCount,
                                note.updatedAt))
                        .from(note)
                        .where(note.opponentUser.id.eq(requestUserId))
                        .orderBy(note.updatedAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
        finalResult.add(loadNoteListResultByNoteCreatingUser);
        finalResult.add(loadNoteListResultByNoteCreatedUser);
        return finalResult;
    }

    /*
    특정 쪽지방에 입장 -> 유저 읽음처리
     */
    @Override
    public void readNoteRoom(long requestUserId, long noteId) {

        queryFactory
                .update(note)
                .set(note.creatingUserUnreadCount, 0)
                .where(note.creatingUser.id.eq(requestUserId)
                        .and(note.id.eq(noteId)))
                .execute();

        queryFactory
                .update(note)
                .set(note.opponentUserUnreadCount, 0)
                .where(note.opponentUser.id.eq(requestUserId)
                        .and(note.id.eq(noteId)))
                .execute();
    }

    // Note 테이블에 최근 메세지 반영
    @Override
    public void updateRecentContent(long unreadUserId, long noteId, String content, String imageLink) {
        // 메세지를 보냈을 때
        if (!content.equals("")) {
            queryFactory
                    .update(note)
                    .set(note.recentContent, content)
                    .set(note.updatedAt, LocalDateTime.now())
                    .where(note.id.eq(noteId))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.opponentUserUnreadCount, note.opponentUserUnreadCount.add(1))
                    .where(note.id.eq(noteId)
                            .and(note.opponentUser.id.eq(unreadUserId)))
                    .execute();
            queryFactory
                    .update(note)
                    .set(note.creatingUserUnreadCount, note.creatingUserUnreadCount.add(1))
                    .where(note.id.eq(noteId)
                            .and(note.creatingUser.id.eq(unreadUserId)))
                    .execute();
            return;
        }
        // 파일을 보냈을 때
        queryFactory
                .update(note)
                .set(note.recentContent, imageLink)
                .set(note.updatedAt, LocalDateTime.now())
                .where(note.id.eq(noteId))
                .execute();
        queryFactory
                .update(note)
                .set(note.opponentUserUnreadCount, note.opponentUserUnreadCount.add(1))
                .where(note.id.eq(noteId)
                        .and(note.opponentUser.id.eq(unreadUserId)))
                .execute();
        queryFactory
                .update(note)
                .set(note.creatingUserUnreadCount, note.creatingUserUnreadCount.add(1))
                .where(note.id.eq(noteId)
                        .and(note.creatingUser.id.eq(unreadUserId)))
                .execute();
    }


    @Override
    public Optional<Note> findNoteByRequestUserAndTargetUserAndProductPost(
            long noteRequestUserId, long targetUserId, long productPostId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(note)
                .where(note.creatingUser.id.eq(noteRequestUserId)
                        .and(note.opponentUser.id.eq(targetUserId))
                        .and(note.productPost.id.eq(productPostId)))
                .fetchOne());
    }
}