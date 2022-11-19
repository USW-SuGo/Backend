package com.usw.sugo.domain.note.notecontent.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteRoomContentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.usw.sugo.domain.note.entity.QNoteContent.noteContent;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoteContentRepositoryImpl implements CustomNoteContentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LoadNoteRoomContentForm> loadNoteRoomAllContentByRoomId(long requestUserId, long noteId, Pageable pageable) {
        // 쪽지방 메세지
        return queryFactory
                .select(Projections.bean(LoadNoteRoomContentForm.class,
                        noteContent.message,
                        noteContent.sender.id.as("messageSenderId"),
                        noteContent.receiver.id.as("messageReceiverId"),
                        noteContent.createdAt.as("messageCreatedAt")))
                .from(noteContent)
                .where(noteContent.noteId.id.eq(noteId))
                .orderBy(noteContent.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
