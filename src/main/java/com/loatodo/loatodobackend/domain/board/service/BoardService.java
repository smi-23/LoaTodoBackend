package com.loatodo.loatodobackend.domain.board.service;

import com.loatodo.loatodobackend.domain.board.dto.BoardReqDto;
import com.loatodo.loatodobackend.domain.board.dto.BoardResDto;
import com.loatodo.loatodobackend.domain.board.entity.Board;
import com.loatodo.loatodobackend.domain.board.repository.BoardRepository;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.service.UserService;
import com.loatodo.loatodobackend.jwt.JwtUtil;
import com.loatodo.loatodobackend.util.Message;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public ResponseEntity<Message> createBoard(BoardReqDto reqDto, HttpServletRequest request){
        Claims claims = jwtUtil.getClaims(request);
        String username = claims.getSubject();
        User user = userService.findUser(username);

        Board board = Board.builder()
                .author(user.getUsername())
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .user(user)
                .build();

        boardRepository.save(board);

        BoardResDto resDto = BoardResDto.builder()
                .id(board.getId())
                .author(board.getAuthor())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .build();

        return new ResponseEntity<>(new Message("게시글이 생성되었습니다.", resDto), HttpStatus.CREATED);
    }
}
