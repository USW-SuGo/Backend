package com.usw.sugo.domain.notecontent.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.dto.QNoteResponseDto_LoadNoteAllContentForm;
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
    public List<LoadNoteAllContentForm> loadNoteRoomAllContentByRoomId(long noteId, Pageable pageable) {
        return queryFactory
                .select(new QNoteResponseDto_LoadNoteAllContentForm(
                        noteContent.noteId.productPost.id, noteContent.id, noteContent.message,
                        noteContent.sender.id, noteContent.receiver.id, noteContent.createdAt,
                        noteFile.id, noteFile.imageLink, noteFile.sender.id,
                        noteFile.receiver.id, noteFile.createdAt
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
