package com.myocean.domain.big5.service;

import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import com.myocean.domain.report.service.ReportService;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.repository.UserPersonaRepository;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.domain.user.service.GameCountService;
import com.myocean.domain.user.service.UserService;
import com.myocean.domain.user.enums.AiStatus;
import com.myocean.domain.gamemanagement.entity.GameSession;
import com.myocean.domain.gamemanagement.repository.GameSessionRepository;
import com.myocean.global.enums.BigCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Big5SchedulerService {

    private final Big5ResultRepository big5ResultRepository;
    private final UserPersonaRepository userPersonaRepository;
    private final UserRepository userRepository;
    private final GameSessionRepository gameSessionRepository;
    private final ReportService reportService;
    private final GameCountService gameCountService;
    private final UserService userService;

    @Scheduled(cron = "0 59 23 * * *", zone = "Asia/Seoul")
    // 매일 23:55에 실행
    @Transactional
    public void calculateAndUpdatePersonas() {
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
        if (!gameCountService.hasEnoughGameCounts(userId)) {
            return;
        }

        // 해당 유저의 게임 타입별 big5 결과 조회
        List<Big5Result> gameResults = big5ResultRepository.findByUserIdAndSourceType(userId, Big5SourceType.GAME);

        if (gameResults.isEmpty()) {
            return;
        }

        // 평균 계산 (null 제외)
        Big5Averages averages = calculateAverages(gameResults);

        // 새로운 UserPersona 생성
        createUserPersona(userId, averages);
    }

    private Big5Averages calculateAverages(List<Big5Result> results) {

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


    private void createUserPersona(Integer userId, Big5Averages averages) {
        List<UserPersona> personas = new ArrayList<>();

        // 각 BigCode별로 UserPersona 생성 (null 값 제외)
        if (averages.o != null) {
            personas.add(UserPersona.builder()
                    .userId(userId)
                    .bigCode(BigCode.O)
                    .score(averages.o.shortValue())
                    .build());
        }

        if (averages.c != null) {
            personas.add(UserPersona.builder()
                    .userId(userId)
                    .bigCode(BigCode.C)
                    .score(averages.c.shortValue())
                    .build());
        }

        if (averages.e != null) {
            personas.add(UserPersona.builder()
                    .userId(userId)
                    .bigCode(BigCode.E)
                    .score(averages.e.shortValue())
                    .build());
        }

        if (averages.a != null) {
            personas.add(UserPersona.builder()
                    .userId(userId)
                    .bigCode(BigCode.A)
                    .score(averages.a.shortValue())
                    .build());
        }

        if (averages.n != null) {
            personas.add(UserPersona.builder()
                    .userId(userId)
                    .bigCode(BigCode.N)
                    .score(averages.n.shortValue())
                    .build());
        }

        // 모든 페르소나 저장
        userPersonaRepository.saveAll(personas);
        log.info("Created {} UserPersona records for user: {}", personas.size(), userId);

        reportService.saveFinalReport(userId, averages);

        // User AI Status를 GENERATED로 업데이트
        userService.updateAiStatus(userId, AiStatus.GENERATED);

        // PersonaProfile 생성 완료 후 Redis 게임 카운트 삭제
        gameCountService.clearGameCounts(userId);
    }

    private static class Big5Averages {
        final Integer o;
        final Integer c;
        final Integer e;
        final Integer a;
        final Integer n;

        Big5Averages(Integer o, Integer c, Integer e, Integer a, Integer n) {
            this.o = o;
            this.c = c;
            this.e = e;
            this.a = a;
            this.n = n;
        }
    }
}