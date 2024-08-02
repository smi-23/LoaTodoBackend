package com.loatodo.loatodobackend.domain.comment.service;

import com.loatodo.loatodobackend.domain.board.dto.BoardResDto;
import com.loatodo.loatodobackend.domain.board.entity.Board;
import com.loatodo.loatodobackend.domain.board.service.BoardServiceUtil;
import com.loatodo.loatodobackend.domain.comment.dto.CommentReqDto;
import com.loatodo.loatodobackend.domain.comment.dto.CommentResDto;
import com.loatodo.loatodobackend.domain.comment.entity.Comment;
import com.loatodo.loatodobackend.domain.comment.repository.CommentRepository;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.service.UserServiceUtil;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.util.Message;
import com.loatodo.loatodobackend.util.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceUtil userServiceUtil;
    private final BoardServiceUtil boardServiceUtil;
    private final CommentServiceUtil commentServiceUtil;
    private final CommentRepository commentRepository;

    // 댓글 생성
    @Transactional
    public ResponseEntity<Message> createComment(CommentReqDto reqDto, Long boardId, HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);

        Board board = boardServiceUtil.findBoard(boardId);

        Comment comment = Comment.builder()
                .writer(user.getUsername())
                .content(reqDto.getContent())
//                .likeCount(0L)
                .board(board)
                .user(user)
                .build();

        commentRepository.save(comment);

        CommentResDto resDto = CommentResDto.of(comment);

        return new ResponseEntity<>(new Message(boardId + "번 게시글에 댓글이 생성되었습니다.", resDto), HttpStatus.CREATED);
    }

    // 모든 댓글 조회
    @Transactional
    public ResponseEntity<Message> getAllComment(Long boardId) {
        List<Comment> commentList = commentServiceUtil.findCommentList(boardId);

        List<CommentResDto> resDtoList = commentList.stream()
                .map(CommentResDto::of)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new Message(boardId + "번 게시글의 전체 댓글을 조회합니다.", resDtoList), HttpStatus.OK);
    }

    // 내가 쓴 모든 댓글 조회
    @Transactional
    public ResponseEntity<Message> getMyComment(HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);
        List<Comment> myCommentList = commentRepository.findByUserId(user.getId());
        if (myCommentList.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        List<CommentResDto> resDtoList = myCommentList.stream()
                .map(CommentResDto::of)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new Message("내가 쓴 전체 댓글을 조회합니다.", resDtoList), HttpStatus.OK);
    }

    // 코멘트 수정
    @Transactional
    public ResponseEntity<Message> updateComment(Long commentId, CommentReqDto reqDto, HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);
        Optional<Comment> optComment = commentRepository.findById(commentId);
        if (optComment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        Comment comment = optComment.get();

        if (user.getRole().equals(UserRole.USER)) {
            if (!user.getId().equals(comment.getUser().getId())) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        }

        comment.update(reqDto);

        Optional<Comment> updatedOptComment = commentRepository.findById(comment.getId());
        if (updatedOptComment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        Comment updatedComment = updatedOptComment.get();

        CommentResDto commentResDto = CommentResDto.of(updatedComment);

        return new ResponseEntity<>(new Message(commentId + "번 댓글이 수정되었습니다.", commentResDto), HttpStatus.OK);
    }

    // 코맨트 삭제
    @Transactional
    public ResponseEntity<Message> deleteComment(Long commentId, HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);
        Optional<Comment> optComment = commentRepository.findById(commentId);
        if (optComment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        Comment comment = optComment.get();

        if (user.getRole().equals(UserRole.USER)) {
            if (!user.getId().equals(comment.getUser().getId())) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        }

        commentRepository.delete(comment);

        return new ResponseEntity<>(new Message(commentId + "번 댓글이 삭제되었습니다.", null), HttpStatus.OK);
    }

    // 해당 코멘트에 추천
//    @Transactional
//    public ResponseEntity<Message> likeComment(Long commentId, HttpServletRequest request) {
//        User user = userServiceUtil.getUser(request);
//        Optional<Comment> otpComment = commentRepository.findById(commentId);
//        if(otpComment.isEmpty()){
//            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
//        }
//
//        Comment comment = otpComment.get();
//
//        comment.incrementViews();
//
//        commentRepository.save(comment);
//
//        // 저장하고 새로 객체를 찾아서 반환해야 하는지? 아니면 적용되어 있는지? => 저장된 값이 반환번
//        CommentResDto resDto = CommentResDto.of(comment);
//
//        return new ResponseEntity<>(new Message(commentId + "번 댓글에 좋아요를 눌렀습니다.", resDto), HttpStatus.OK);
//    }

    // 해당 코멘트에 비추천
//    @Transactional
//    public ResponseEntity<Message> dislikeComment(Long commentId, HttpServletRequest request) {
//        User user = userServiceUtil.getUser(request);
//        Optional<Comment> otpComment = commentRepository.findById(commentId);
//        if(otpComment.isEmpty()){
//            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
//        }
//
//        Comment comment = otpComment.get();
//
//        comment.disincrementViews();
//
//        commentRepository.save(comment);
//
//        // 저장하고 새로 객체를 찾아서 반환해야 하는지? 아니면 적용되어 있는지?
//        CommentResDto resDto = CommentResDto.of(comment);
//
//
//        return new ResponseEntity<>(new Message(commentId + "번 댓글에 싫어요를 눌렀습니다.", resDto), HttpStatus.OK);
//    }

}
