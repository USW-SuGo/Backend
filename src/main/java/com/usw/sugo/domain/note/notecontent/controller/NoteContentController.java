package com.usw.sugo.domain.note.notecontent.controller;

import com.usw.sugo.domain.note.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;
import com.usw.sugo.domain.note.notecontent.service.NoteContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-content")
public class NoteContentController {

    /*
    채팅방 인덱스로, 특정 채팅방 컨텐츠/파일 전송하기
    (Get) localhost:8080/chatting/?roomId={}&page={}&size={}
    */
    private final NoteContentService noteContentService;

    @PostMapping("/")
    public ResponseEntity<Object> sendNoteContent(@RequestBody @Valid SendNoteContentForm sendNoteContentForm) {

        noteContentService.sendContent(sendNoteContentForm);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }
}
