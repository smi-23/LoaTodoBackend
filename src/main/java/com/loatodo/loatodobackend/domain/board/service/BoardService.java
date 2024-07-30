package com.loatodo.loatodobackend.domain.board.service;

import com.loatodo.loatodobackend.domain.board.dto.BoardReqDto;
import com.loatodo.loatodobackend.domain.board.dto.BoardResDto;
import com.loatodo.loatodobackend.domain.board.entity.Board;
import com.loatodo.loatodobackend.domain.board.repository.BoardRepository;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.service.UserService;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.jwt.JwtUtil;
import com.loatodo.loatodobackend.util.Message;
import com.loatodo.loatodobackend.util.UserRole;
import io.jsonwebtoken.Claims;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public ResponseEntity<Message> createBoard(BoardReqDto reqDto, HttpServletRequest request) {
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
                .modifiedAt(board.getModifiedAt())
                .build();

        return new ResponseEntity<>(new Message("게시글이 생성되었습니다.", resDto), HttpStatus.CREATED);
    }

    public ResponseEntity<Message> getAllBoardList() {
        List<Board> boardList = boardRepository.findAllByOrderByCreatedAtDesc();

        List<BoardResDto> boardResDtoList = boardList.stream()
                .map(board -> BoardResDto.builder()
                        .id(board.getId())
                        .author(board.getAuthor())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .createdAt(board.getCreatedAt())
                        .modifiedAt(board.getModifiedAt())
                        .build())
                .collect(Collectors.toList());

        return new ResponseEntity<>(new Message("전체 게시글을 조회합니다.", boardResDtoList), HttpStatus.OK);
    }

    public ResponseEntity<Message> getMyBoardList(HttpServletRequest request) {
        Claims claims = jwtUtil.getClaims(request);
        String username = claims.getSubject();
        User user = userService.findUser(username);
        List<Board> myBoardList = boardRepository.findAllByAuthorOrderByCreatedAtDesc(username);

        List<BoardResDto> boardResDtoList = myBoardList.stream()
                .map(board -> BoardResDto.builder()
                        .id(board.getId())
                        .author(board.getAuthor())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .createdAt(board.getCreatedAt())
                        .modifiedAt(board.getModifiedAt())
                        .build())
                .collect(Collectors.toList());
        return new ResponseEntity<>(new Message(username + "님의 전체 게시글을 조회합니다.", boardResDtoList), HttpStatus.OK);
    }

    public ResponseEntity<Message> updateBoard(Long id, BoardReqDto reqDto, HttpServletRequest request){
        Claims claims = jwtUtil.getClaims(request);
        String username = claims.getSubject();
        User user = userService.findUser(username);
        Optional<Board> optBoard = boardRepository.findById(id);
        if (optBoard.isEmpty()) {
            throw new CustomException(ErrorCode.BOARD_NOT_FOUND);
        }

        Board board = optBoard.get();
        UserRole userRole = user.getRole();
        if (userRole == UserRole.USER){
            if(!board.getUser().getId().equals(user.getId())){
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        }
        board.update(reqDto);
        boardRepository.save(board);

        Board updatedBoard = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        BoardResDto resDto = BoardResDto.builder()
                .id(updatedBoard.getId())
                .author(updatedBoard.getAuthor())
                .title(updatedBoard.getTitle())
                .content(updatedBoard.getContent())
                .createdAt(updatedBoard.getCreatedAt())
                .modifiedAt(updatedBoard.getModifiedAt())
                .build();

        return new ResponseEntity<>(new Message(username + "님의 해당 게시글을 수정합니다.", resDto), HttpStatus.OK);
    }

    public ResponseEntity<Message> deleteBoard(Long id, HttpServletRequest request) {
        Claims claims = jwtUtil.getClaims(request);
        String username = claims.getSubject();
        User user = userService.findUser(username);
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            throw new CustomException(ErrorCode.BOARD_NOT_FOUND);
        }

        UserRole userRole = user.getRole();
        if (userRole == UserRole.USER){
            if(!board.get().getUser().getId().equals(user.getId())){
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        }
        boardRepository.deleteById(id);

        return new ResponseEntity<>(new Message(username + "님의 해당 게시글을 삭제합니다.", null), HttpStatus.OK);
    }
}
