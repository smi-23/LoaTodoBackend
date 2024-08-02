package com.loatodo.loatodobackend.domain.comment.service;

import com.loatodo.loatodobackend.domain.comment.entity.Comment;
import com.loatodo.loatodobackend.domain.comment.repository.CommentRepository;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.service.UserServiceUtil;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.jwt.JwtUtil;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceUtil {
    private final UserServiceUtil userServiceUtil;
    private final JwtUtil jwtUtill;
    private final CommentRepository commentRepository;

public List<Comment> findCommentList (Long boardId){
    List<Comment> commentList = commentRepository.findByBoardId(boardId);
    if(commentList.isEmpty()){
        throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }
    return commentList;
}

}
