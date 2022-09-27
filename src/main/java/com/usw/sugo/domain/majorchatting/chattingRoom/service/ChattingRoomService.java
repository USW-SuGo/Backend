//package com.usw.sugo.domain.majorchatting.service;
//
//import com.usw.sugo.domain.majorchatting.ChattingRoom;
//import com.usw.sugo.domain.majorchatting.repository.ChattingRoomRepository;
//import com.usw.sugo.domain.majoruser.User;
//import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
//import com.usw.sugo.domain.status.Status;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ChattingRoomService {
//
//    private final ChattingRoomRepository chattingRoomRepository;
//    private final UserRepository userRepository;
//
//
//
//    public void createChattingRoom(long buyerId, long sellerId) {
//        User buyer = userRepository.findById(buyerId).get();
//        User seller = userRepository.findById(sellerId).get();
//
//        ChattingRoom chattingRoom = ChattingRoom.builder()
//                .roomId(UUID.randomUUID().toString())
//                .buyer(buyer)
//                .seller(seller)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .status(Status.AVAILABLE.getAuthority())
//                .build();
//    }
//}
