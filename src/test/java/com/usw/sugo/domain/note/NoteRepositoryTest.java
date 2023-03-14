//package com.usw.sugo.domain.note;
//
//import com.usw.sugo.domain.note.note.controller.dto.NoteResponseDto.LoadNoteListForm;
//import com.usw.sugo.domain.note.note.repository.NoteRepository;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional
//public class NoteRepositoryTest {
//
//    @Autowired
//    NoteRepository noteRepository;
//
//
//    @Test
//    void test() {
//        List<List<LoadNoteListForm>> lists = noteRepository
//            .loadNoteListByUserId(3, new PageRequest(
//                1, 2, new Sort(new ArrayList<>()))
//            );
//    }
//}
