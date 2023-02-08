package com.usw.sugo.domain.note.note;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.util.basetime.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private Integer creatingUserUnreadCount;

    @Column
    private Integer opponentUserUnreadCount;

    public void updateRecentContent(String recentContent) {
        this.recentContent = recentContent;
    }

    public void updateUserUnreadCount(User user) {
        if (this.creatingUser.equals(user)) {
            this.creatingUserUnreadCount += 1;
        } else if (this.opponentUser.equals(user)) {
            this.opponentUserUnreadCount += 1;
        }
    }

    public void resetUserUnreadCount(User user) {
        if (this.creatingUser.equals(user)) {
            this.creatingUserUnreadCount = 0;
        } else if (this.opponentUser.equals(user)) {
            this.opponentUserUnreadCount = 1;
        }
    }
}
