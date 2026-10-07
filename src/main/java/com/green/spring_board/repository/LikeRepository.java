package com.green.spring_board.repository;

import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.Like;
import com.green.spring_board.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LikeRepository extends JpaRepository<Like, Integer>{

    Optional<Like> findByUserIdAndBoardId(int user_id, int board_id);
    List<Like> findByBoardId(int boardId);
    Boolean existsByUserIdAndBoardId (int user_id, int board_id);
}
