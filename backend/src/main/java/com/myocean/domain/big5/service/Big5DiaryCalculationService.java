package com.myocean.domain.big5.service;

import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

//다이어리 분석 결과를 Big5Result에 저장
@Service
@RequiredArgsConstructor
@Slf4j
public class Big5DiaryCalculationService {

    private final Big5ResultRepository big5ResultRepository;

    @Transactional
    public void saveDiaryBig5Result(Integer userId, Integer diaryId, Map<String, Double> big5Scores) {
        log.info("다이어리 Big5 결과 저장 시작 - userId: {}, diaryId: {}", userId, diaryId);

        // Double (0.0~1.0) → Integer (0~100) 변환
        Integer resultO = convertToInteger(big5Scores.get("openness"));
        Integer resultC = convertToInteger(big5Scores.get("conscientiousness"));
        Integer resultE = convertToInteger(big5Scores.get("extraversion"));
        Integer resultA = convertToInteger(big5Scores.get("agreeableness"));
        Integer resultN = convertToInteger(big5Scores.get("neuroticism"));

        Big5Result big5Result = Big5Result.builder()
                .userId(userId)
                .sourceType(Big5SourceType.DIARY)
                .sourceId(diaryId.longValue())
                .resultO(resultO)
                .resultC(resultC)
                .resultE(resultE)
                .resultA(resultA)
                .resultN(resultN)
                .build();

        big5ResultRepository.save(big5Result);

        log.info("다이어리 Big5 결과 저장 완료 - userId: {}, diaryId: {}, O:{}, C:{}, E:{}, A:{}, N:{}",
                userId, diaryId, resultO, resultC, resultE, resultA, resultN);
    }


    private Integer convertToInteger(Double value) {
        if (value == null) {
            log.warn("Big5 점수가 null입니다. 0으로 설정합니다.");
            return 0;
        }
        return (int) Math.round(value * 100);
    }
}
