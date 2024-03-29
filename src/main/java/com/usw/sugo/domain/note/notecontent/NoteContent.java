package com.usw.sugo.domain.note.notecontent;

import com.usw.sugo.domain.note.note.Note;
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
@NoArgsConstructor
@AllArgsConstructor
public class NoteContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "note_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;

    @JoinColumn(name = "sender_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @JoinColumn(name = "receiver_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    @Column
    private String message;

    @Column
    private String imageLink;

    @CreatedDate
    private LocalDateTime createdAt;
}
