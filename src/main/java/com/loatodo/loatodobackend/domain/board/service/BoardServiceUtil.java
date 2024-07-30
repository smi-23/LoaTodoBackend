package com.loatodo.loatodobackend.domain.board.service;

import com.loatodo.loatodobackend.domain.board.entity.Board;
import com.loatodo.loatodobackend.domain.board.repository.BoardRepository;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceUtil {
    private final BoardRepository boardRepository;

    public Board findBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    public void validateBoardListNotEmpty(List<Board> boardList) {
        if (boardList.isEmpty()) {
            throw new CustomException(ErrorCode.BOARD_NOT_FOUND);
        }
    }

    public void checkRole(User user, Board board) {
        UserRole userRole = user.getRole();
        if (userRole == UserRole.USER) {
            if (!board.getUser().getId().equals(user.getId())) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        }
    }
}
