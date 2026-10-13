package com.green.spring_board.repository;

import com.green.spring_board.entity.RefreshToken;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {

    void deleteByUserId(int userId);
    Optional<RefreshToken> findByToken(String Token);

    //expirationDatetime이 매개변수 시간보다 이전인 모든 데이터 삭제
    void deleteAllByExpirationDatetimeBefore(LocalDateTime localDateTime);

}
