package com.usw.sugo.domain.note.entity;

import com.usw.sugo.domain.productpost.entity.ProductPost;
import com.usw.sugo.domain.user.entity.User;
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
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "product_post_id")
    @OneToOne
    private ProductPost productPost;

    @JoinColumn(name = "creating_user_id")
    @OneToOne
    private User creatingUser;

    @JoinColumn(name = "opponent_user_id")
    @OneToOne
    private User opponentUser;

    @Column
    private String creatingUserNickname;

    @Column
    private String opponentUserNickname;

    @Column
    private String recentContent;

    @Column
    private int creatingUserUnreadCount;

    @Column
    private int opponentUserUnreadCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
