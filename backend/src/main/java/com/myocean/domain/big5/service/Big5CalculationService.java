package com.myocean.domain.big5.service;

import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Big5 분석 결과를 Big5Result 테이블에 저장하는 통합 서비스
 * Diary, MyChat, GameSession 등 모든 소스 타입에서 사용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Big5CalculationService {

    private final Big5ResultRepository big5ResultRepository;

    /**
     * Big5 점수를 Big5Result 테이블에 저장
     *
     * @param userId 사용자 ID
     * @param sourceType 소스 타입 (DIARY, MY_CHAT, GAME_SESSION)
     * @param sourceId 소스 ID (diaryId, messageId, sessionId 등)
     * @param big5Scores Big5 점수 맵 (openness, conscientiousness, extraversion, agreeableness, neuroticism)
     */
    @Transactional
    public void saveBig5Result(Integer userId, Big5SourceType sourceType, Long sourceId, Map<String, Double> big5Scores) {
        log.info("Big5 결과 저장 시작 - userId: {}, sourceType: {}, sourceId: {}", userId, sourceType, sourceId);

        // Double (0.0~1.0) → Integer (0~100) 변환
        Integer resultO = convertToInteger(big5Scores.get("openness"));
        Integer resultC = convertToInteger(big5Scores.get("conscientiousness"));
        Integer resultE = convertToInteger(big5Scores.get("extraversion"));
        Integer resultA = convertToInteger(big5Scores.get("agreeableness"));
        Integer resultN = convertToInteger(big5Scores.get("neuroticism"));

        Big5Result big5Result = Big5Result.builder()
                .userId(userId)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .resultO(resultO)
                .resultC(resultC)
                .resultE(resultE)
                .resultA(resultA)
                .resultN(resultN)
                .build();

        big5ResultRepository.save(big5Result);

        log.info("Big5 결과 저장 완료 - userId: {}, sourceType: {}, sourceId: {}, O:{}, C:{}, E:{}, A:{}, N:{}",
                userId, sourceType, sourceId, resultO, resultC, resultE, resultA, resultN);
    }

    private Integer convertToInteger(Double value) {
        if (value == null) {
            log.warn("Big5 점수가 null입니다. 0으로 설정합니다.");
            return 0;
        }
        return (int) Math.round(value * 100);
    }
}
