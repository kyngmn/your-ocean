package com.myocean.domain.diary.service;

import com.myocean.domain.diary.converter.DiaryAnalysisConverter;
import com.myocean.domain.diary.converter.DiaryConverter;
import com.myocean.domain.diary.dto.request.DiaryCreateRequest;
import com.myocean.domain.diary.dto.response.DiaryResponse;
import com.myocean.domain.diary.dto.response.DiaryCalendarResponse;
import com.myocean.domain.diary.entity.Diary;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.user.repository.UserRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final DiaryAnalysisService diaryAnalysisService;
    private final DiaryAsyncService diaryAsyncService;
    private final DiaryAnalysisSummaryService diaryAnalysisSummaryService;

    @Transactional
    public DiaryResponse createDiary(Integer userId, DiaryCreateRequest request) {
        log.info("[SYNC] createDiary 시작 - userId: {}", userId);

        // 해당 날짜에 이미 다이어리가 있는지 확인
        if (diaryRepository.existsByUserIdAndDiaryDateAndNotDeleted(userId, request.getDiaryDate())) {
            log.warn("다이어리 중복 - userId: {}, diaryDate: {}", userId, request.getDiaryDate());
            throw new GeneralException(ErrorStatus.DIARY_ALREADY_EXISTS_FOR_DATE);
        }

        Diary diary = Diary.builder()
                .user(userRepository.getReferenceById(userId))
                .title(request.getTitle())
                .content(request.getContent())
                .diaryDate(request.getDiaryDate())
                .build();

        Diary savedDiary = diaryRepository.save(diary);

        // Summary를 PROCESSING 상태로 미리 생성 (분석 실패 시에도 상태 업데이트 가능하도록)
        diaryAnalysisSummaryService.createInitialSummary(savedDiary.getId());

        // 비동기 AI 분석 시작
        diaryAsyncService.asyncAnalyzeDiary(userId, savedDiary.getId(), savedDiary.getTitle(), savedDiary.getContent());

        log.info("[SYNC] createDiary 완료 - diaryId: {}", savedDiary.getId());
        return DiaryConverter.toResponse(savedDiary);
    }

    public DiaryResponse getDiaryById(Integer userId, Integer diaryId) {
        Diary diary = diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        return DiaryConverter.toResponse(diary);
    }

    /**
     * 특정 일에 해당하는 Diary 조회
     */
    public DiaryResponse getDiaryByDate(Integer userId, String diaryDate) {
        LocalDate date = parseDate(diaryDate);
        Diary diary = diaryRepository.findByUserIdAndDiaryDateAndNotDeleted(userId, date)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        return DiaryConverter.toResponse(diary);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_DATE_FORMAT);
        }
    }

    @Transactional
    public void deleteDiaryById(Integer userId, Integer diaryId) {
        Diary diary = diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        diary.delete();
    }

    /**
     * 특정 년,달에서 Diary를 쓴 날짜만 조회하기
     */
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

    private List<LocalDate> extractUniqueSortedDates(List<Diary> diaries) {
        return diaries.stream()
                .map(Diary::getDiaryDate)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Diary 분석 결과 조회
     */
    public DiaryAnalysisResponse getDiaryAnalysis(Integer userId, Integer diaryId) {
        // 다이어리 권한 확인
        diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        // 실제 분석 상태 조회
        String analysisStatus = diaryAnalysisSummaryService.getAnalysisStatus(diaryId).name();

        // 저장된 분석 메시지들 조회
        List<DiaryAnalysisMessage> analysisMessages = diaryAnalysisService.getStoredAnalysisMessages(diaryId);

        // 분석 결과가 있으면 메시지와 요약 정보를 포함해서 반환
        if (!analysisMessages.isEmpty()) {
            return buildDiaryAnalysisResponse(diaryId, analysisMessages, analysisStatus);
        }

        // 분석 결과가 없으면 상태만 반환
        log.info("분석 결과 없음 - diaryId: {}, status: {}", diaryId, analysisStatus);
        return DiaryAnalysisResponse.builder()
                .diaryId(diaryId)
                .status(analysisStatus)
                .build();
    }

    /**
     * DiaryAnalysisMessage 리스트를 DiaryAnalysisResponse로 변환
     */
    private DiaryAnalysisResponse buildDiaryAnalysisResponse(Integer diaryId, List<DiaryAnalysisMessage> analysisMessages, String status) {
        List<DiaryAnalysisResponse.OceanMessage> oceanMessages = analysisMessages.stream()
                .map(DiaryAnalysisConverter::toOceanMessage)
                .toList();

        DiaryAnalysisResponse.AnalysisSummary summary = diaryAnalysisService.getAnalysisSummary(diaryId);

        return DiaryAnalysisResponse.builder()
                .diaryId(diaryId)
                .status(status)
                .oceanMessages(oceanMessages)
                .summary(summary)
                .build();
    }

}