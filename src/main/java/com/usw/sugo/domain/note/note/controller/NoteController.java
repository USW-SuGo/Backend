package com.usw.sugo.domain.note.note.controller;

import com.usw.sugo.domain.note.Note;
import com.usw.sugo.domain.note.note.dto.NoteRequestDto.CreateNoteRequest;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.User;
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
    public ResponseEntity<Object> createRoom(@RequestBody CreateNoteRequest request) {

        ProductPost productPost = productPostRepository.findById(request.getProductPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_BAD_REQUEST));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        Note note = Note.builder()
                .productPost(productPost)
                .sellerId(seller)
                .buyerId(buyer)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        noteRepository.save(note);

        // 거래 시도 횟수 + 1
        userRepository.plusCountTradeAttempt(seller.getId(), buyer.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<String, Long>() {{put("noteId", note.getId());}});
    }

    /*
    유저 인덱스로, 채팅방 목록 불러오기
    채팅방 인덱스, 상호 유저간의 인덱스, 상호 유저간의 닉네임, 최근 채팅시간, 최근 채팅 메세지
    */
    @GetMapping("/list")
    public ResponseEntity<Object> loadAllChattingRoomByUserId(
            @RequestHeader String authorization, Pageable pageable) {

        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        User requestUser = userRepository.findById(userId).orElseThrow(()
                -> new CustomException(ErrorCode.USER_NOT_EXIST));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteRepository.loadChattingRoomListByUserId(requestUser.getId(), pageable));
    }

    /*
    채팅방 인덱스로, 특정 채팅방 컨텐츠/파일 조회하기
    (Get) localhost:8080/chatting/?roomId={}&page={}&size={}
    */
    @GetMapping("/")
    public ResponseEntity<Object> loadAllChattingRoomContentByRoomId(@RequestParam Long roomId, Pageable pageable) {

        Map<String, Object> result = new HashMap<>();

        result.put("Note", noteRepository.loadChattingRoomFormByRoomId(roomId));
        result.put("NoteContent", noteRepository.loadChattingRoomMessageFormByRoomId(roomId, pageable));
        result.put("NoteFile", noteRepository.loadChattingRoomFileFormByRoomId(roomId, pageable));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
