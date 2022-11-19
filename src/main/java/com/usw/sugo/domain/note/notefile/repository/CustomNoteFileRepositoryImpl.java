package com.usw.sugo.domain.note.notefile.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteRoomFileForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.usw.sugo.domain.note.entity.QNoteFile.noteFile;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoteFileRepositoryImpl implements CustomNoteFileRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LoadNoteRoomFileForm> loadNoteRoomAllFileByRoomId(long requestUserId, long noteId, Pageable pageable) {
        // 쪽지방 파일
        return queryFactory
                        .select(Projections.bean(LoadNoteRoomFileForm.class,
                                noteFile.imageLink,
                                noteFile.sender.id.as("fileSenderId"),
                                noteFile.receiver.id.as("fileReceiverId"),
                                noteFile.createdAt.as("fileCreatedAt")))
                        .from(noteFile)
                        .where(noteFile.noteId.id.eq(noteId))
                        .orderBy(noteFile.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
    }
}
