package com.green.spring_board.repository;

import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    Integer id(int id);
    boolean existsByEmail(String email);
}
