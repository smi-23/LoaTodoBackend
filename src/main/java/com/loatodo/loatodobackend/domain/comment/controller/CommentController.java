package com.loatodo.loatodobackend.domain.comment.controller;

import com.loatodo.loatodobackend.domain.comment.dto.CommentReqDto;
import com.loatodo.loatodobackend.domain.comment.service.CommentService;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/comment")
public class CommentController {
    private final CommentService commentService;
    // 해당 게시글에 코멘트 생성
    @PostMapping("/create/{boardId}")
    public ResponseEntity<Message> createComment (@RequestBody CommentReqDto reqDto, @PathVariable Long boardId, HttpServletRequest request){
        return commentService.createComment(reqDto, boardId, request);
    }

    // 해당 게시글의 코멘트 전체 조회
    @GetMapping("/all/{boardId}")
    public ResponseEntity<Message> getAllComment (@PathVariable Long boardId){
        return commentService.getAllComment(boardId);
    }

    // 내가 남긴 코멘트 조회(마이페이지)
    @GetMapping("/my")
    public ResponseEntity<Message> getMyComment (HttpServletRequest request){
        return commentService.getMyComment(request);
    }

    // 코멘트 수정
    @PatchMapping("/update/{commentId}")
    public ResponseEntity<Message> updateComment (@RequestBody CommentReqDto reqDto, @PathVariable Long commentId, HttpServletRequest request){
        return commentService.updateComment(commentId, reqDto, request);
    }

    // 코멘트 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Message> deleteComment (@PathVariable Long commentId, HttpServletRequest request){
        return commentService.deleteComment(commentId, request);
    }

    // 해당 코멘트에 추천
//    @PostMapping("/like/{commentId}")
//    public ResponseEntity<Message> likeComment (@PathVariable Long commentId, HttpServletRequest request){
//        return commentService.likeComment(commentId, request);
//    }

    // 해당 코멘트에 비추천
//    @PostMapping("/dislike/{commentId}")
//    public ResponseEntity<Message> dislikeComment (@PathVariable Long commentId, HttpServletRequest request){
//        return commentService.dislikeComment(commentId, request);
//    }
}
