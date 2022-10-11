package com.usw.sugo.domain.majorchatting;


import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majoruser.User;
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

    @Column
    private String recentContent;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private String status;

    public static ChattingRoom create() {
        ChattingRoom chattingRoom = new ChattingRoom();
        chattingRoom.uuid = UUID.randomUUID().toString();
        return chattingRoom;
    }
}
