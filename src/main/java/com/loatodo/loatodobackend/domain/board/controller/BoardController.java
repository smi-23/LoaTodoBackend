package com.loatodo.loatodobackend.domain.board.controller;

import com.loatodo.loatodobackend.domain.board.dto.BoardReqDto;
import com.loatodo.loatodobackend.domain.board.service.BoardService;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/board")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/create")
    public ResponseEntity<Message> createBoard(@RequestBody BoardReqDto reqDto, HttpServletRequest request) {
        return boardService.createBoard(reqDto, request);
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllBoardList() {
        return boardService.getAllBoardList();
    }

    @GetMapping("/my")
    public ResponseEntity<Message> getMyBoardList(HttpServletRequest request) {
        return boardService.getMyBoardList(request);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Message> readBoard(@PathVariable Long boardId) {
        return boardService.readBoard(boardId);
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<Message> updateBoard(@PathVariable Long id, @RequestBody BoardReqDto reqDto, HttpServletRequest request) {
        return boardService.updateBoard(id, reqDto, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Message> deleteBoard(@PathVariable Long id,  HttpServletRequest request) {
        return boardService.deleteBoard(id, request);
    }
}
