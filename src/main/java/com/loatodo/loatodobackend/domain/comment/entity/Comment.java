package com.loatodo.loatodobackend.domain.comment.entity;

import com.loatodo.loatodobackend.domain.board.entity.Board;
import com.loatodo.loatodobackend.domain.comment.dto.CommentReqDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.util.Timestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment extends Timestamp {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String writer;

    @Column
    private String content;

//    @Column
//    private Long likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    private Board board;

    public void update(CommentReqDto reqdto) {
        if (reqdto.getContent() != null) {
            this.content = reqdto.getContent();
        }
    }

//    public void incrementViews() {
//        this.likeCount++;
//    }
//
//    public void disincrementViews() {
//        this.likeCount--;
//    }

}
