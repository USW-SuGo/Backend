package com.usw.sugo.domain.productpost.productpostfile;

import com.usw.sugo.domain.productpost.productpost.ProductPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


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
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductPost productPost;

    @CreatedDate
    private LocalDateTime createdAt;

    public void updateImageLink(List<String> imageLinks) {
        StringBuilder finalLink = new StringBuilder();
        for (String link : imageLinks) {
            finalLink.append(link);
        }
        this.imageLink = finalLink.toString();
    }
}
