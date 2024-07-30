package com.loatodo.loatodobackend.domain.board.dto;

import com.loatodo.loatodobackend.domain.board.entity.Board;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BoardResDto {
    private Long id;
    private String author;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static BoardResDto of(Board board) {
        return BoardResDto.builder()
                .id(board.getId())
                .author(board.getAuthor())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .build();
    }
}
