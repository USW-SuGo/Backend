package com.usw.sugo.domain.note.note.controller;

import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.note.note.dto.NoteRequestDto.CreateNoteRequest;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.note.notefile.repository.NoteFileRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final ProductPostRepository productPostRepository;
    private final NoteRepository noteRepository;
    private final NoteContentRepository noteContentRepository;
    private final NoteFileRepository noteFileRepository;
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

        noteRepository.findNoteByRequestUserAndTargetUserAndProductPost(
                creatingRequestUserId, opponentUser.getId(), productPost.getId());

        Note note = Note.builder()
                .productPost(productPost)
                .creatingUserId(creatingRequestUser)
                .creatingUserNickname(creatingRequestUser.getNickname())
                .creatingUserUnreadCount(0)
                .opponentUserId(opponentUser)
                .opponentUserNickname(opponentUser.getNickname())
                .opponentUserUnreadCount(0)
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

        // QueryDSL 조회 후 2차원 리스트를 분해한다.
        List<List<LoadNoteListForm>> noteListResult = noteRepository.loadNoteListByUserId(requestUser.getId(), pageable);
        List<LoadNoteListForm> loadNoteListFormRequestUserIsCreatingNote = noteListResult.get(0);
        List<LoadNoteListForm> loadNoteListFormsRequestUserIsCreatedNote = noteListResult.get(1);

        // 분해한 2차원 리스트를 임시 보관한다. (DTO 내의 속성인, 최근 채팅 시각을 기준으로 정렬을 해주어야 하기 때문)
        List<LoadNoteListForm> tempResult = new ArrayList<>();
        tempResult.addAll(loadNoteListFormRequestUserIsCreatingNote);
        tempResult.addAll(loadNoteListFormsRequestUserIsCreatedNote);
        // 분해한 2차원 리스트를 임시 보관한다. (DTO 내의 속성인, 최근 채팅 시각을 기준으로 정렬을 해주어야 하기 때문)

        Stream<LoadNoteListForm> finalResult = tempResult
                .stream()
                .sorted(Comparator.comparing(LoadNoteListForm::getRecentChattingDate)
                        .reversed());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(finalResult);
    }

    /*
    채팅방 인덱스로, 특정 채팅방 컨텐츠/파일 조회하기
    (Get) localhost:8080/note/?roomId={}&page={}&size={}
    */
    @GetMapping("/")
    public ResponseEntity<Object> loadAllNoteContentByRoomId(
            @RequestHeader String authorization, @RequestParam Long noteId, Pageable pageable) {

        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        User requestUser = userRepository.findById(userId).orElseThrow(()
                -> new CustomException(ErrorCode.USER_NOT_EXIST));

        // 요청유저 유저 읽음처리
        noteRepository.readNoteRoom(requestUser.getId(), noteId);

        List<LoadNoteAllContentForm> loadNoteAllContentForms =
                noteContentRepository.loadNoteRoomAllContentByRoomId(requestUser.getId(), noteId, pageable);

        for (LoadNoteAllContentForm loadNoteAllContentForm : loadNoteAllContentForms) {
            loadNoteAllContentForm.setRequestUserId(requestUser.getId());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loadNoteAllContentForms);
    }
}
