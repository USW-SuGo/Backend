package com.usw.sugo.domain.chatting;


import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String uuid;

    @JoinColumn(name = "product_post_id")
    @OneToOne
    private ProductPost productPost;

    @JoinColumn(name = "seller_id")
    @OneToOne
    private User sellerId;

    @JoinColumn(name = "buyer_id")
    @OneToOne
    private User buyerId;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static ChattingRoom create() {
        ChattingRoom chattingRoom = new ChattingRoom();
        chattingRoom.uuid = UUID.randomUUID().toString();
        return chattingRoom;
    }
}
