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
public class ChattingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String roomValue;

    @JoinColumn(name = "seller_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User sellerId;

    @JoinColumn(name = "buyer_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User buyerId;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private String status;
}
