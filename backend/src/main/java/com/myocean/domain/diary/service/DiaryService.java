package com.myocean.domain.diary.service;

import com.myocean.domain.diary.dto.converter.DiaryConverter;
import com.myocean.domain.diary.dto.request.DiaryCreateRequest;
import com.myocean.domain.diary.dto.response.DiaryResponse;
import com.myocean.domain.diary.dto.response.DiaryCalendarResponse;
import com.myocean.domain.diary.entity.Diary;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.global.ai.AiClientService;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final AiClientService aiClientService;

    @Transactional
    public DiaryResponse createDiary(Integer userId, DiaryCreateRequest request) {
        Diary diary = Diary.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .diaryDate(request.getDiaryDate())
                .build();

        Diary savedDiary = diaryRepository.save(diary);

        // TODO: Kafka로 AI 분석 요청 전송
        sendToAiAnalysis(savedDiary);

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

    public Object analyzeDiary(Integer userId, Integer diaryId) {
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

    public Object getDiaryAnalysis(Integer userId, Integer diaryId) {
        try {
            // 1. 다이어리 권한 확인
            diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

            // 2. TODO: 저장된 분석 결과 조회 (현재는 실시간 분석)
            log.info("다이어리 분석 결과 조회 - userId: {}, diaryId: {}", userId, diaryId);
            
            // 임시로 실시간 분석 수행 (추후 저장된 결과 조회로 변경)
            return analyzeDiary(userId, diaryId);

        } catch (Exception e) {
            log.error("다이어리 분석 결과 조회 실패 - userId: {}, diaryId: {}, error: {}", userId, diaryId, e.getMessage(), e);
            throw new RuntimeException("다이어리 분석 결과 조회 중 오류가 발생했습니다.", e);
        }
    }

    private void sendToAiAnalysis(Diary diary) {
        // TODO: 현재는 로그만 출력, 추후 비동기 처리 구현 예정
        log.info("Sending diary {} to AI analysis (NOT IMPLEMENTED YET)", diary.getId());
        
        // 구현 예정:
        // 1. Kafka Producer로 메시지 전송
        // 2. AI 서버로 직접 HTTP 요청  
        // 3. 비동기 처리로 성능 최적화
    }
}