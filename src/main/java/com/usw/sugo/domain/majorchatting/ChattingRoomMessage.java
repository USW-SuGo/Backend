package com.usw.sugo.domain.majorchatting;


import com.usw.sugo.domain.majoruser.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "chatting_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChattingRoom chattingRoomId;

    @Column
    private String message;

    @JoinColumn(name = "sender")
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @JoinColumn(name = "receiver")
    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    @CreatedDate
    private LocalDateTime createdAt;


    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private String status;
}
