package com.loatodo.loatodobackend.domain.board.service;

import com.loatodo.loatodobackend.domain.board.dto.BoardReqDto;
import com.loatodo.loatodobackend.domain.board.dto.BoardResDto;
import com.loatodo.loatodobackend.domain.board.entity.Board;
import com.loatodo.loatodobackend.domain.board.repository.BoardRepository;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.service.UserServiceUtil;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserServiceUtil userServiceUtil;
    private final BoardServiceUtil boardServiceUtil;

    public ResponseEntity<Message> createBoard(BoardReqDto reqDto, HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);

        Board board = Board.builder()
                .author(user.getUsername())
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .view(0L)
                .user(user)
                .build();

        boardRepository.save(board);

        BoardResDto resDto = BoardResDto.of(board);

        return new ResponseEntity<>(new Message("게시글이 생성되었습니다.", resDto), HttpStatus.CREATED);
    }

    public ResponseEntity<Message> getAllBoardList() {
        List<Board> boardList = boardRepository.findAllByOrderByCreatedAtDesc();
        boardServiceUtil.validateBoardListNotEmpty(boardList);

        List<BoardResDto> boardResDtoList = boardList.stream()
                .map(BoardResDto::of)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new Message("전체 게시글을 조회합니다.", boardResDtoList), HttpStatus.OK);
    }

    public ResponseEntity<Message> getMyBoardList(HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);

        List<Board> myBoardList = boardRepository.findAllByAuthorOrderByCreatedAtDesc(user.getUsername());
        boardServiceUtil.validateBoardListNotEmpty(myBoardList);

        List<BoardResDto> boardResDtoList = myBoardList.stream()
                .map(BoardResDto::of)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new Message(user.getUsername() + "님의 전체 게시글을 조회합니다.", boardResDtoList), HttpStatus.OK);
    }

    public ResponseEntity<Message> readBoard(Long boardId){
        Board board = boardServiceUtil.findBoard(boardId);

        board.incrementViews();

        boardRepository.save(board);

        BoardResDto resDto = BoardResDto.of(board);

        return new ResponseEntity<>(new Message(board.getId() + "번 게시글을 조회합니다.", resDto), HttpStatus.OK);
    }

    public ResponseEntity<Message> updateBoard(Long id, BoardReqDto reqDto, HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);

        Board board = boardServiceUtil.findBoard(id);

        boardServiceUtil.checkRole(user, board);

        board.update(reqDto);

        boardRepository.save(board);

        Board updatedBoard = boardServiceUtil.findBoard(id);

        BoardResDto resDto = BoardResDto.of(updatedBoard);

        return new ResponseEntity<>(new Message(user.getUsername() + "님의 해당 게시글을 수정합니다.", resDto), HttpStatus.OK);
    }

    public ResponseEntity<Message> deleteBoard(Long id, HttpServletRequest request) {
        User user = userServiceUtil.getUser(request);

        Board board = boardServiceUtil.findBoard(id);

        boardServiceUtil.checkRole(user, board);

        boardRepository.deleteById(id);

        return new ResponseEntity<>(new Message(user.getUsername() + "님의 해당 게시글을 삭제합니다.", null), HttpStatus.OK);
    }
}

