package com.loatodo.loatodobackend.domain.board.repository;

import com.loatodo.loatodobackend.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository <Board, Long>{
    List<Board> findAllByOrderByCreatedAtDesc();
    List<Board> findAllByAuthorOrderByCreatedAtDesc(String author);
}
