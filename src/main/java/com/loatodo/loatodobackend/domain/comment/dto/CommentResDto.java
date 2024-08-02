package com.loatodo.loatodobackend.domain.comment.dto;

import com.loatodo.loatodobackend.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentResDto {
    private Long id;
    private String writer;
    private String content;
//    private Long likeCount;

    public static CommentResDto of(Comment comment){
        return CommentResDto.builder()
                .id(comment.getId())
                .writer(comment.getWriter())
                .content(comment.getContent())
//                .likeCount(comment.getLikeCount())
                .build();
    }
}
