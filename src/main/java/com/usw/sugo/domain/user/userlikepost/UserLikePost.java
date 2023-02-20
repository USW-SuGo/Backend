package com.usw.sugo.domain.user.userlikepost;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import com.usw.sugo.domain.user.user.User;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserLikePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_id")
    private ProductPost productPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_file_id")
    private ProductPostFile productPostFile;

    @CreatedDate
    private LocalDateTime createdAt;
}
