package com.myocean.domain.big5.service;

import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.repository.UserPersonaRepository;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.domain.user.service.UserGameCountService;
import com.myocean.domain.user.service.UserPersonaGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Big5SchedulerService {

    private final Big5ResultRepository big5ResultRepository;
    private final UserPersonaRepository userPersonaRepository;
    private final UserRepository userRepository;
    private final UserGameCountService userGameCountService;
    private final UserPersonaGenerationService personaGenerationService;

    //    @Scheduled(cron = "0 59 23 * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "0 59 23 * * *", zone = "Asia/Seoul")
    @Transactional
    public void calculateAndUpdatePersonas() {
        log.info("스케줄링 돌아요");
        // 모든 유저 조회
        List<Integer> userIds = userRepository.findAll().stream()
                .map(user -> user.getId())
                .collect(Collectors.toList());

        for (Integer userId : userIds) {
            try {
                calculatePersonaForUser(userId);
            } catch (Exception e) {
            }
        }
    }

    private void calculatePersonaForUser(Integer userId) {
        // 이미 persona가 생성된 유저는 스킵
        List<UserPersona> existingPersonas = userPersonaRepository.findByUserIdAndDeletedAtIsNull(userId);
        if (!existingPersonas.isEmpty()) {
            return;
        }

        // Redis에서 게임 카운트 확인
        if (!userGameCountService.hasEnoughGameCounts(userId)) {
            return;
        }

        // 해당 유저의 게임 타입별 big5 결과 조회
        List<Big5Result> gameResults = big5ResultRepository.findByUserIdAndSourceType(userId, Big5SourceType.GAME_SESSION);

        if (gameResults.isEmpty()) {
            return;
        }

        // PersonaGenerationService를 사용하여 UserPersona 생성
        personaGenerationService.createUserPersona(userId, gameResults);
    }

}