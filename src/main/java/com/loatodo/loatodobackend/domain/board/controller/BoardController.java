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

//    @GetMapping("/")
//    public ResponseEntity<Message> getAllBoardList() {
//        return boardService.getAllBoardList();
//    }
//
//    @GetMapping("/")
//    public ResponseEntity<Message> getMyBoardList(HttpServletRequest request) {
//        return boardService.getMyBoardList(request);
//    }

    @PostMapping("/create")
    public ResponseEntity<Message> createBoard(@RequestBody BoardReqDto reqDto, HttpServletRequest request) {
        return boardService.createBoard(reqDto, request);
    }
}
