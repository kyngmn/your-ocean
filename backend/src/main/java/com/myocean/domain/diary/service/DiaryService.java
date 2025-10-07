package com.myocean.domain.diary.service;

import com.myocean.domain.diary.converter.DiaryConverter;
import com.myocean.domain.diary.dto.request.DiaryCreateRequest;
import com.myocean.domain.diary.dto.response.DiaryResponse;
import com.myocean.domain.diary.dto.response.DiaryCalendarResponse;
import com.myocean.domain.diary.entity.Diary;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.global.ai.AiClientService;
import com.myocean.global.openai.diaryanalysis.service.DiaryAnalysisRefinementService;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final AiClientService aiClientService;
    private final DiaryAnalysisService diaryAnalysisService;
    private final DiaryAnalysisRefinementService diaryAnalysisRefinementService;
    private final DiaryAsyncService diaryAsyncService;

    @Transactional
    public DiaryResponse createDiary(Integer userId, DiaryCreateRequest request) {
        log.info("🟢 [SYNC] createDiary 시작 - userId: {}, thread: {}",
                userId, Thread.currentThread().getName());

        Diary diary = Diary.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .diaryDate(request.getDiaryDate())
                .build();

        Diary savedDiary = diaryRepository.save(diary);
        log.info("🟢 [SYNC] 다이어리 저장 완료 - diaryId: {}, thread: {}",
                savedDiary.getId(), Thread.currentThread().getName());

        // 다이어리 저장 후 비동기로 AI 분석 시작
        log.info("🟢 [SYNC] 비동기 AI 분석 호출 - diaryId: {}, thread: {}",
                savedDiary.getId(), Thread.currentThread().getName());
        diaryAsyncService.asyncAnalyzeDiary(userId, savedDiary.getId(), savedDiary.getTitle(), savedDiary.getContent());

        log.info("🟢 [SYNC] createDiary 응답 반환 - diaryId: {}, thread: {}",
                savedDiary.getId(), Thread.currentThread().getName());
        return DiaryConverter.toResponse(savedDiary);
    }


    public DiaryResponse getDiaryByDate(Integer userId, String diaryDate) {
        LocalDate date = parseDate(diaryDate);
        Diary diary = diaryRepository.findByUserIdAndDiaryDateAndNotDeleted(userId, date)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        return DiaryConverter.toResponse(diary);
    }

    public DiaryResponse getDiaryById(Integer userId, Integer diaryId) {
        Diary diary = diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        return DiaryConverter.toResponse(diary);
    }

    @Transactional
    public void deleteDiaryById(Integer userId, Integer diaryId) {
        Diary diary = diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        diary.delete();
    }

    public DiaryCalendarResponse getDiaryCalendar(Integer userId, String yearMonth) {
        YearMonth ym = parseYearMonth(yearMonth);
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Diary> diaries = diaryRepository.findByUserIdAndDateRangeAndNotDeleted(userId, startDate, endDate);
        List<LocalDate> diaryDates = extractUniqueSortedDates(diaries);

        return DiaryCalendarResponse.of(yearMonth, diaryDates);
    }

    private YearMonth parseYearMonth(String yearMonth) {
        try {
            return YearMonth.parse(yearMonth);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_YEAR_MONTH_FORMAT);
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_YEAR_MONTH_FORMAT); // TODO: 날짜 형식 오류 상태 추가
        }
    }

    private List<LocalDate> extractUniqueSortedDates(List<Diary> diaries) {
        return diaries.stream()
                .map(Diary::getDiaryDate)
                .distinct()
                .sorted()
                .toList();
    }

    public Map<String, Object> analyzeDiary(Integer userId, Integer diaryId) {
        try {
            // 1. 다이어리 권한 확인
            Diary diary = diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

            // 2. AI 서버로 분석 요청
            log.info("AI 서버로 다이어리 분석 요청 - userId: {}, diaryId: {}", userId, diaryId);
            Map<String, Object> analysisResult = aiClientService.analyzeDiary(userId, diaryId, diary.getContent(), diary.getTitle());

            return analysisResult;

        } catch (Exception e) {
            log.error("다이어리 분석 실패 - userId: {}, diaryId: {}, error: {}", userId, diaryId, e.getMessage(), e);
            throw new RuntimeException("다이어리 분석 중 오류가 발생했습니다.", e);
        }
    }

    public DiaryAnalysisResponse getDiaryAnalysis(Integer userId, Integer diaryId) {
        try {
            // 1. 다이어리 권한 확인
            diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

            // 2. 저장된 OCEAN 분석 결과 조회
            log.info("다이어리 분석 결과 조회 - userId: {}, diaryId: {}", userId, diaryId);

            // 저장된 분석 메시지들 조회
            List<DiaryAnalysisMessage> analysisMessages = diaryAnalysisService.getStoredAnalysisMessages(userId, diaryId);

            // 분석 결과가 없으면 실시간 분석 수행
            if (analysisMessages.isEmpty()) {
                log.info("저장된 분석 결과가 없어 실시간 분석 수행 - diaryId: {}", diaryId);

                // 다이어리 정보 조회
                Diary diary = diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

                Map<String, Object> analysisResult = analyzeDiary(userId, diaryId);

                // OpenAI로 분석 결과 다듬기
                Map<String, Object> refinedResult = diaryAnalysisRefinementService.refineAnalysisResult(
                    diary.getTitle(), diary.getContent(), analysisResult);

                diaryAnalysisService.parseAndSaveAnalysisResult(userId, diaryId, refinedResult);

                // 다시 조회
                analysisMessages = diaryAnalysisService.getStoredAnalysisMessages(userId, diaryId);
            }

            // DiaryAnalysisResponse로 변환하여 반환
            return buildDiaryAnalysisResponse(diaryId, analysisMessages);

        } catch (Exception e) {
            log.error("다이어리 분석 결과 조회 실패 - userId: {}, diaryId: {}, error: {}", userId, diaryId, e.getMessage(), e);
            throw new RuntimeException("다이어리 분석 결과 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * DiaryAnalysisMessage 리스트를 DiaryAnalysisResponse로 변환
     */
    private DiaryAnalysisResponse buildDiaryAnalysisResponse(Integer diaryId, List<DiaryAnalysisMessage> analysisMessages) {
        try {
            // OCEAN 메시지들을 변환
            List<DiaryAnalysisResponse.OceanMessage> oceanMessages = analysisMessages.stream()
                    .map(this::convertToOceanMessage)
                    .toList();

            // 분석 요약 정보 조회
            DiaryAnalysisResponse.AnalysisSummary summary = diaryAnalysisService.getAnalysisSummary(diaryId);

            return DiaryAnalysisResponse.builder()
                    .diaryId(diaryId)
                    .oceanMessages(oceanMessages)
                    .summary(summary)
                    .build();

        } catch (Exception e) {
            log.error("DiaryAnalysisResponse 변환 실패 - diaryId: {}, error: {}", diaryId, e.getMessage(), e);
            throw new RuntimeException("분석 결과 변환 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * DiaryAnalysisMessage를 OceanMessage로 변환
     */
    private DiaryAnalysisResponse.OceanMessage convertToOceanMessage(DiaryAnalysisMessage message) {
        // Actor ID로 성격 요소명 매핑 (대문자로 변경)
        Map<Integer, String[]> actorMapping = Map.of(
                1, new String[]{"OPENNESS", "개방성"},
                2, new String[]{"CONSCIENTIOUSNESS", "성실성"},
                3, new String[]{"EXTRAVERSION", "외향성"},
                4, new String[]{"AGREEABLENESS", "친화성"},
                5, new String[]{"NEUROTICISM", "신경성"}
        );

        String[] actorInfo = actorMapping.get(message.getSenderActorId());
        String personality = actorInfo != null ? actorInfo[0] : "UNKNOWN";
        String personalityName = actorInfo != null ? actorInfo[1] : "알 수 없음";

        return DiaryAnalysisResponse.OceanMessage.builder()
                .id(message.getId())
                .personality(personality)
                .personalityName(personalityName)
                .message(message.getMessage())
                .messageOrder(message.getMessageOrder())
                .createdAt(message.getCreatedAt())
                .build();
    }

}