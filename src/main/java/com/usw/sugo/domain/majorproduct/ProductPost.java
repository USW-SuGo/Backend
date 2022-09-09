package com.usw.sugo.domain.majorproduct;

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
public class ProductPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String contactPlace;

    // 작성자 인덱스
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 해당 게시글에 포함된 이미지 링크
    @ManyToOne
    @JoinColumn(name = "product_post_file_id")
    private ProductPostFile productPostFile;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private String status;
}
