package com.usw.sugo.domain.note.note.controller;

import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.note.note.dto.NoteRequestDto.CreateNoteRequest;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.entity.ProductPost;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final ProductPostRepository productPostRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final JwtResolver jwtResolver;
    /*
     쪽지 방 만들기
     */
    @PostMapping
    public ResponseEntity<Object> createRoom(@RequestHeader String authorization, @RequestBody CreateNoteRequest request) {

        long creatingRequestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        ProductPost productPost = productPostRepository.findById(request.getProductPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_BAD_REQUEST));

        User creatingRequestUser = userRepository.findById(creatingRequestUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        User opponentUser = userRepository.findById(request.getOpponentUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));


        Note note = Note.builder()
                .productPost(productPost)
                .creatingUserId(creatingRequestUser)
                .creatingUserNickname(creatingRequestUser.getNickname())
                .opponentUserId(opponentUser)
                .opponentUserNickname(opponentUser.getNickname())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        noteRepository.save(note);

        // 거래 시도 횟수 + 1
        userRepository.plusCountTradeAttempt(creatingRequestUser.getId(), opponentUser.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<String, Long>() {{
                    put("noteId", note.getId());
                }});
    }

    /*
    유저 인덱스로, 채팅방 목록 불러오기
    채팅방 인덱스, 상호 유저간의 인덱스, 상호 유저간의 닉네임, 최근 채팅시간, 최근 채팅 메세지
    */
    @GetMapping("/list")
    public ResponseEntity<Object> loadAllNoteListByUserId(
            @RequestHeader String authorization, Pageable pageable) {

        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        User requestUser = userRepository.findById(userId).orElseThrow(()
                -> new CustomException(ErrorCode.USER_NOT_EXIST));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteRepository.loadNoteListByUserId(userId, requestUser.getId(), pageable));
    }

    /*
    채팅방 인덱스로, 특정 채팅방 컨텐츠/파일 조회하기
    (Get) localhost:8080/note/?roomId={}&page={}&size={}
    */
    @GetMapping("/")
    public ResponseEntity<Object> loadAllNoteContentByRoomId(@RequestParam Long noteId, Pageable pageable) {

        Map<String, Object> result = new HashMap<>();

        result.put("NoteContent", noteRepository.loadNoteMessageFormByRoomId(noteId, pageable));
        result.put("NoteFile", noteRepository.loadNoteFileFormByRoomId(noteId, pageable));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
