package com.usw.sugo.domain.productpost.productpost;

import com.usw.sugo.domain.user.user.User;
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
public class ProductPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Integer price;

    @Column
    private String contactPlace;

    @Column
    private String category;

    // 작성자 인덱스
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private Boolean status;

    public void updateUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatusToTrue() {
        this.status = true;
    }

    public void updateStatusToFalse() {
        this.status = false;
    }

    public void updateProductPost(String title, String content, Integer price, String contactPlace, String category) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.contactPlace = contactPlace;
        this.category = category;
    }
}
