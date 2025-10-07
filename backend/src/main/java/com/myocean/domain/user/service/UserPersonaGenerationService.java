package com.myocean.domain.user.service;

import com.myocean.domain.big5.calculator.Big5AverageCalculator;
import com.myocean.domain.big5.dto.Big5AverageScores;
import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.report.service.PersonalityAnalysisService;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.repository.UserPersonaRepository;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.domain.user.enums.AiStatus;
import com.myocean.global.enums.BigCode;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserPersonaGenerationService {

    private final UserPersonaRepository userPersonaRepository;
    private final UserRepository userRepository;
    private final Big5AverageCalculator big5AverageCalculator;
    private final PersonalityAnalysisService personalityAnalysisService;
    private final UserService userService;
    private final UserGameCountService userGameCountService;

    @Transactional
    public void createUserPersona(Integer userId, List<Big5Result> gameResults) {
        log.info("=== UserPersona 생성 시작 - userId: {}, gameResults: {} ===", userId, gameResults.size());

        // 1. User 엔티티 조회
        User user = findUserById(userId);

        // 2. Big5 게임 평균 점수 계산
        Big5AverageScores averageScores = big5AverageCalculator.calculateAverages(gameResults);
        log.info("Big5 게임 평균 계산 완료 - O:{}, C:{}, E:{}, A:{}, N:{}",
                averageScores.getO(), averageScores.getC(), averageScores.getE(),
                averageScores.getA(), averageScores.getN());

        // 3. UserPersona 5개 레코드 생성 및 저장
        saveUserPersonas(user, averageScores);

        // 4. OpenAI 성격 분석 리포트 생성
        generatePersonalityReport(userId, averageScores);

        // 5. User 상태 업데이트 및 Redis 정리
        finalizePersonaGeneration(userId);

        log.info("=== UserPersona 생성 완료 - userId: {} ===", userId);
    }


    private User findUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        log.info("User 엔티티 조회 완료 - userId: {}", userId);
        return user;
    }

    private void saveUserPersonas(User user, Big5AverageScores averageScores) {
        Map<String, Integer> scoresMap = averageScores.toMap();

        for (Map.Entry<String, Integer> entry : scoresMap.entrySet()) {
            UserPersona persona = UserPersona.builder()
                    .user(user)
                    .bigCode(BigCode.fromString(entry.getKey()))
                    .score(entry.getValue().shortValue())
                    .build();
            userPersonaRepository.save(persona);

            log.info("  💾 UserPersona 저장 - userId: {}, bigCode: {}, score: {}",
                    user.getId(), entry.getKey(), entry.getValue());
        }

        log.info("UserPersona 5개 레코드 저장 완료 - userId: {}", user.getId());
    }


    private void generatePersonalityReport(Integer userId, Big5AverageScores averageScores) {
        Map<String, Integer> scoresMap = averageScores.toMap();

        log.info("OpenAI 성격 분석 시작 - userId: {}, gameScores: {}", userId, scoresMap);
        personalityAnalysisService.generatePersonalityReport(userId, scoresMap);
        log.info("OpenAI 성격 분석 완료 - userId: {}", userId);
    }


    private void finalizePersonaGeneration(Integer userId) {
        // User AI Status 업데이트
        userService.updateAiStatus(userId, AiStatus.GENERATED);
        log.info("User AI Status 업데이트 완료 - userId: {}, status: GENERATED", userId);

        // Redis 게임 카운트 삭제
        userGameCountService.clearGameCounts(userId);
        log.info("Redis 게임 카운트 삭제 완료 - userId: {}", userId);
    }

}