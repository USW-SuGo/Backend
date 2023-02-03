package com.usw.sugo.domain.user.useremailauth;


import com.usw.sugo.domain.user.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private String payload;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    private Boolean status;

    public void confirmToken() {
        this.status = true;
    }
}
