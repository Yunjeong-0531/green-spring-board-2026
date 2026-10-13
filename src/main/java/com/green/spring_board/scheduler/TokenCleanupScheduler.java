package com.green.spring_board.scheduler;


import com.green.spring_board.repository.AccessTokenBlacklistRepository;
import com.green.spring_board.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class TokenCleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;


    @Scheduled(cron="0 0 3 * * *")
    @Transactional
    public void cleanTokens(){
        LocalDateTime now = LocalDateTime.now();
        log.warn("토큰 정리 스케쥴러가 시작됩니다. 실행 시작 시간 : {}",now);
        //만료시간이 지난 리프레시 토큰 제거
        refreshTokenRepository.deleteAllByExpirationDatetimeBefore(now);
        //만료시간 지난 엑세스블랙리스트 토큰 제거
        accessTokenBlacklistRepository.deleteAllByExpirationDatetimeBefore(now);
        log.warn("토큰 정리 스케줄러 작업이 성공적으로 마무리되었습니다.");
    }

}
