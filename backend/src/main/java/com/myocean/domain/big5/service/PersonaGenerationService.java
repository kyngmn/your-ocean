package com.myocean.domain.big5.service;

import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.repository.UserPersonaRepository;
import com.myocean.domain.user.service.GameCountService;
import com.myocean.domain.user.service.UserService;
import com.myocean.domain.user.enums.AiStatus;
import com.myocean.domain.gamemanagement.entity.GameSession;
import com.myocean.domain.gamemanagement.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaGenerationService {

    private final UserPersonaRepository userPersonaRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PersonalityAnalysisService personalityAnalysisService;
    private final UserService userService;
    private final GameCountService gameCountService;

    @Transactional
    public UserPersona createUserPersona(Integer userId, List<Big5Result> gameResults) {
        log.info("📊 PersonaGenerationService 시작 - userId: {}, gameResults 수: {}", userId, gameResults.size());

        // 평균 계산
        Big5Averages averages = calculateBig5Averages(gameResults);
        log.info("📊 Big5 평균 계산 완료 - averages: O={}, C={}, E={}, A={}, N={}",
                averages.o, averages.c, averages.e, averages.a, averages.n);

        // UserPersona 생성 및 저장
        UserPersona persona = UserPersona.builder()
                .userId(userId)
                .userO(averages.o)
                .userC(averages.c)
                .userE(averages.e)
                .userA(averages.a)
                .userN(averages.n)
                .build();

        UserPersona savedPersona = userPersonaRepository.save(persona);
        log.info("📊 UserPersona 저장 완료 - personaId: {}", savedPersona.getId());

        // Big5 평균값을 Map으로 변환하여 OpenAI 성격 분석 리포트 생성
        Map<String, Integer> gameScores = averages.toMap();
        log.info("📊 OpenAI 성격 분석 시작 - gameScores: {}", gameScores);
        personalityAnalysisService.generatePersonalityReport(userId, gameScores);

        // User AI Status를 GENERATED로 업데이트
        userService.updateAiStatus(userId, AiStatus.GENERATED);
        log.info("📊 User AI Status 업데이트 완료 - GENERATED");

        // PersonaProfile 생성 완료 후 Redis 게임 카운트 삭제
        gameCountService.clearGameCounts(userId);
        log.info("📊 Redis 게임 카운트 삭제 완료");

        log.info("📊 PersonaGenerationService 완료 - userId: {}", userId);
        return savedPersona;
    }

    public Big5Averages calculateBig5Averages(List<Big5Result> results) {
        // 각 차원별로 null이 아닌 값들을 수집하여 평균 계산
        List<Integer> oValues = results.stream()
                .map(Big5Result::getResultO)
                .filter(val -> val != null)
                .collect(Collectors.toList());

        List<Integer> cValues = results.stream()
                .map(Big5Result::getResultC)
                .filter(val -> val != null)
                .collect(Collectors.toList());

        List<Integer> eValues = results.stream()
                .map(Big5Result::getResultE)
                .filter(val -> val != null)
                .collect(Collectors.toList());

        List<Integer> aValues = results.stream()
                .map(Big5Result::getResultA)
                .filter(val -> val != null)
                .collect(Collectors.toList());

        List<Integer> nValues = results.stream()
                .map(Big5Result::getResultN)
                .filter(val -> val != null)
                .collect(Collectors.toList());

        return new Big5Averages(
                calculateAverageInteger(oValues),
                calculateAverageInteger(cValues),
                calculateAverageInteger(eValues),
                calculateAverageInteger(aValues),
                calculateAverageInteger(nValues)
        );
    }

    private Integer calculateAverageInteger(List<Integer> values) {
        if (values.isEmpty()) {
            return null;
        }
        double average = values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        return (int) Math.round(average);
    }

    public boolean hasEnoughGameSessions(Integer userId, List<Big5Result> gameResults) {
        // Big5Result의 sourceId로 GameSession 조회하여 게임 타입별로 그룹핑
        List<Long> sourceIds = gameResults.stream()
                .map(Big5Result::getSourceId)
                .collect(Collectors.toList());

        List<GameSession> gameSessions = gameSessionRepository.findByIdIn(sourceIds);

        // 게임 타입별로 세션 수 카운트
        Map<String, Long> gameTypeCount = gameSessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getGameType().name(),
                        Collectors.counting()
                ));

        // 각 게임 타입별로 최소 3개 이상인지 확인
        boolean gngEnough = gameTypeCount.getOrDefault("GNG", 0L) >= 3;
        boolean ugEnough = gameTypeCount.getOrDefault("UG", 0L) >= 3;
        boolean bartEnough = gameTypeCount.getOrDefault("BART", 0L) >= 3;

        return gngEnough && ugEnough && bartEnough;
    }

    public static class Big5Averages {
        public final Integer o;
        public final Integer c;
        public final Integer e;
        public final Integer a;
        public final Integer n;

        public Big5Averages(Integer o, Integer c, Integer e, Integer a, Integer n) {
            this.o = o;
            this.c = c;
            this.e = e;
            this.a = a;
            this.n = n;
        }

        public Map<String, Integer> toMap() {
            return Map.of(
                    "O", o != null ? o : 50,
                    "C", c != null ? c : 50,
                    "E", e != null ? e : 50,
                    "A", a != null ? a : 50,
                    "N", n != null ? n : 50
            );
        }
    }
}