package com.usw.sugo.domain.note.note;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.util.basetime.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column
    private Boolean creatingUserStatus;

    @Column
    private Boolean opponentUserStatus;

    public void updateRecentContent(String recentContent) {
        this.recentContent = recentContent;
    }

    public void updateUserUnreadCountBySendMessage(User messageSendingUser) {
        if (messageSendingUser.getId().equals(creatingUser.getId())) {
            creatingUserUnreadCount = 0;
            opponentUserUnreadCount += 1;
        } else if (messageSendingUser.getId().equals(opponentUser.getId())) {
            creatingUserUnreadCount += 1;
            opponentUserUnreadCount = 0;
        }
    }

    public void initUserUnreadCountByEnteredNote(User enteredNoteUser) {
        if (enteredNoteUser.getId().equals(creatingUser.getId())) {
            creatingUserUnreadCount = 0;
        } else if (enteredNoteUser.getId().equals(opponentUser.getId())) {
            opponentUserUnreadCount = 0;
        }
    }

    public void convertFalseCreatingUserStatus() {
        this.creatingUserStatus = false;
    }

    public void convertFalseOpponentUserStatus() {
        this.opponentUserStatus = false;
    }
}
