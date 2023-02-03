package com.usw.sugo.domain.productpost.productpostfile;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
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
@NoArgsConstructor
@AllArgsConstructor
public class ProductPostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String imageLink;

    @JoinColumn(name = "product_post_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ProductPost productPost;

    @CreatedDate
    private LocalDateTime createdAt;
}
