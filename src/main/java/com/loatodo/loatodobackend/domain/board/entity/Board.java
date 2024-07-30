package com.loatodo.loatodobackend.domain.board.entity;

import com.loatodo.loatodobackend.domain.board.dto.BoardReqDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.util.Timestamp;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boards")
public class Board extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
