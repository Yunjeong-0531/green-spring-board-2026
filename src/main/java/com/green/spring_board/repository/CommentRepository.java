package com.green.spring_board.repository;

import com.green.spring_board.entity.Comment;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByBoardIdAndIsDeletedFalse(int boardId);

}
