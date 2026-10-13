package com.green.spring_board.repository;

import com.green.spring_board.entity.RefreshToken;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {

    void deleteByUserId(int userId);
    Optional<RefreshToken> findByToken(String Token);
}
