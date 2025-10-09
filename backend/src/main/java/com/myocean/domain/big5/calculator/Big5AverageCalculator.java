package com.myocean.domain.big5.calculator;

import com.myocean.domain.big5.dto.Big5AverageScores;
import com.myocean.domain.big5.entity.Big5Result;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class Big5AverageCalculator {

    public static Big5AverageScores calculateAverages(List<Big5Result> results) {
        log.debug("Big5 평균 계산 시작 - 결과 수: {}", results.size());

        // 각 차원별로 null이 아닌 값들을 수집하여 평균 계산
        Integer avgO = calculateAverageForDimension(
                results.stream()
                        .map(Big5Result::getResultO)
                        .filter(val -> val != null)
                        .collect(Collectors.toList())
        );

        Integer avgC = calculateAverageForDimension(
                results.stream()
                        .map(Big5Result::getResultC)
                        .filter(val -> val != null)
                        .collect(Collectors.toList())
        );

        Integer avgE = calculateAverageForDimension(
                results.stream()
                        .map(Big5Result::getResultE)
                        .filter(val -> val != null)
                        .collect(Collectors.toList())
        );

        Integer avgA = calculateAverageForDimension(
                results.stream()
                        .map(Big5Result::getResultA)
                        .filter(val -> val != null)
                        .collect(Collectors.toList())
        );

        Integer avgN = calculateAverageForDimension(
                results.stream()
                        .map(Big5Result::getResultN)
                        .filter(val -> val != null)
                        .collect(Collectors.toList())
        );

        log.debug("Big5 평균 계산 완료 - O:{}, C:{}, E:{}, A:{}, N:{}", avgO, avgC, avgE, avgA, avgN);

        return new Big5AverageScores(avgO, avgC, avgE, avgA, avgN);
    }

    private static Integer calculateAverageForDimension(List<Integer> values) {
        if (values.isEmpty()) {
            return null;
        }
        double average = values.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        return (int) Math.round(average);
    }
}
