package com.usw.sugo.domain.note.notefile.repository;

import static com.usw.sugo.domain.note.notefile.QNoteFile.noteFile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.note.dto.QNoteResponseDto_LoadNoteAllContentForm;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoteFileRepositoryImpl implements CustomNoteFileRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LoadNoteAllContentForm> loadNoteRoomAllContentByRoomId(Long noteId,
        Pageable pageable) {
        return queryFactory
            .select(new QNoteResponseDto_LoadNoteAllContentForm(
                noteFile.id, noteFile.imageLink, noteFile.sender.id, noteFile.receiver.id,
                noteFile.createdAt
            ))
            .from(noteFile)
            .where(noteFile.note.id.eq(noteId))
            .orderBy(noteFile.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
