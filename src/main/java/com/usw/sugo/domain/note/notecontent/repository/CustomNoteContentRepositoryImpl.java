package com.usw.sugo.domain.note.notecontent.repository;

import static com.usw.sugo.domain.note.notecontent.QNoteContent.noteContent;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.notecontent.controller.dto.QNoteContentResponseDto_LoadNoteAllContentForm;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoteContentRepositoryImpl implements CustomNoteContentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LoadNoteAllContentForm> loadAllNoteContentByNoteId(Long noteId, Pageable pageable) {
        return queryFactory
            .select(new QNoteContentResponseDto_LoadNoteAllContentForm(
                noteContent.note.productPost.id,
                noteContent.id, noteContent.message, noteContent.imageLink,
                noteContent.sender.id, noteContent.receiver.id,
                noteContent.createdAt
            ))
            .from(noteContent)
            .where(noteContent.note.id.eq(noteId))
            .orderBy(noteContent.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public void deleteByNoteContent(NoteContent inputNoteContent) {
        queryFactory
            .delete(noteContent)
            .where(noteContent.eq(inputNoteContent))
            .execute();
    }
}
