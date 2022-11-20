package com.usw.sugo.domain.note.notecontent.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.usw.sugo.domain.note.entity.QNoteContent.noteContent;
import static com.usw.sugo.domain.note.entity.QNoteFile.noteFile;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoteContentRepositoryImpl implements CustomNoteContentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LoadNoteAllContentForm> loadNoteRoomAllContentByRoomId(long requestUserId, long noteId, Pageable pageable) {
        // 쪽지방 메세지
        return queryFactory
                .select(Projections.bean(LoadNoteAllContentForm.class,
                        noteContent.id.as("noteContentId"),
                        noteContent.message,
                        noteContent.sender.id.as("messageSenderId"),
                        noteContent.receiver.id.as("messageReceiverId"),
                        noteContent.createdAt.as("messageCreatedAt"),
                        noteFile.id.as("noteFileId"),
                        noteFile.imageLink, noteFile.sender.id.as("fileSenderId"),
                        noteFile.receiver.id.as("fileReceiverId"),
                        noteFile.createdAt.as("fileCreatedAt")
                ))
                .from(noteContent)
                .leftJoin(noteFile)
                .on(noteContent.noteId.eq(noteFile.noteId))
                .where(noteContent.noteId.id.eq(noteId))
                .orderBy(noteContent.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}
