package com.usw.sugo.domain.note.notecontent.repository;

import static com.usw.sugo.domain.note.notecontent.QNoteContent.noteContent;
import static com.usw.sugo.domain.note.notefile.QNoteFile.noteFile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.note.dto.QNoteResponseDto_LoadNoteAllContentForm;
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
            .select(new QNoteResponseDto_LoadNoteAllContentForm(
                noteContent.note.productPost.id,
                noteContent.id, noteContent.message, noteContent.sender.id, noteContent.receiver.id,
                noteContent.createdAt
            ))
            .from(noteContent)
            .where(noteContent.note.id.eq(noteId))
            .orderBy(noteContent.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
