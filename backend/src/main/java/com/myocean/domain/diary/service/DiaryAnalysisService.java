package com.myocean.domain.diary.service;

import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.domain.diary.entity.DiaryAnalysisSummary;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.diary.repository.DiaryAnalysisMessageRepository;
import com.myocean.domain.diary.repository.DiaryAnalysisSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryAnalysisService {

    private final DiaryAnalysisMessageRepository diaryAnalysisMessageRepository;
    private final DiaryAnalysisSummaryRepository diaryAnalysisSummaryRepository;

    // OCEAN 모델 Actor ID 상수
    private static final int OPENNESS_ACTOR_ID = 1;      // O
    private static final int CONSCIENTIOUSNESS_ACTOR_ID = 2; // C
    private static final int EXTROVERSION_ACTOR_ID = 3;  // E (올바른 철자: Extroversion)
    private static final int AGREEABLENESS_ACTOR_ID = 4; // A
    private static final int NEUROTICISM_ACTOR_ID = 5;   // N

    /**
     * AI 서버에서 받은 분석 결과를 파싱하고 DB에 저장
     * @param userId 사용자 ID
     * @param diaryId 다이어리 ID
     * @param analysisResult AI 서버에서 받은 분석 결과
     */
    @Transactional(readOnly = false)
    public void parseAndSaveAnalysisResult(Integer userId, Integer diaryId, Map<String, Object> analysisResult) {
        try {
            log.info("AI 분석 결과 파싱 시작 - userId: {}, diaryId: {}", userId, diaryId);
            log.debug("AI 분석 결과 원본 데이터: {}", analysisResult);

            // 실제 AI 서버 분석 결과 구조:
            // {
            //   "success": true,
            //   "data": {
            //     "messages": [],
            //     "user_input": "새로운 직장에 적응하기 어려워요",
            //     "big5_scores": {"Extraversion": 0.7, "Agreeableness": 0.6, ...},
            //     "domain_classification": "NEUROTICISM",
            //     "agent_responses": {
            //       "Extraversion": "외향성 페르소나 메시지",
            //       "Agreeableness": "친화성 페르소나 메시지",
            //       "Conscientiousness": "성실성 페르소나 메시지",
            //       "Neuroticism": "신경성 페르소나 메시지",
            //       "Openness": "개방성 페르소나 메시지"
            //     },
            //     "reasoning_chain": [...],
            //     "final_conclusion": "최종 결론 메시지"
            //   }
            // }

            if (analysisResult == null) {
                log.error("분석 결과가 null - userId: {}, diaryId: {}", userId, diaryId);
                return;
            }

            // AI 서버는 /ai/analyze/diary 엔드포인트에서 직접 응답 구조를 반환
            Map<String, Object> actualData = analysisResult;
            log.debug("AI 서버 응답 구조: {}", analysisResult.keySet());

            // OpenAI 처리 후 응답인 경우에만 data 필드에서 추출
            if (analysisResult.containsKey("data") && analysisResult.get("data") instanceof Map) {
                actualData = (Map<String, Object>) analysisResult.get("data");
                log.debug("data 필드에서 추출됨");
            }

            Object agentResponsesObj = actualData.get("agent_responses");
            if (!(agentResponsesObj instanceof Map)) {
                log.error("agent_responses가 Map 타입이 아니거나 비어있음 - userId: {}, diaryId: {}, type: {}, actualData keys: {}",
                        userId, diaryId, agentResponsesObj != null ? agentResponsesObj.getClass().getSimpleName() : "null", actualData.keySet());
                return;
            }

            Map<String, Object> agentResponses = (Map<String, Object>) agentResponsesObj;

            // OCEAN 모델 기반으로 5개 메시지 저장
            saveOceanAnalysisMessages(userId, diaryId, agentResponses);

            // DiaryAnalysisSummary 저장 (big5_scores, domain_classification, final_conclusion)
            saveDiaryAnalysisSummary(diaryId, actualData);

            log.info("AI 분석 결과 파싱 및 저장 완료 - userId: {}, diaryId: {}", userId, diaryId);

        } catch (Exception e) {
            log.error("AI 분석 결과 파싱 실패 - userId: {}, diaryId: {}, error: {}, analysisResult: {}",
                    userId, diaryId, e.getMessage(), analysisResult, e);
            throw new RuntimeException("분석 결과 파싱 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * OCEAN 모델 기반으로 5개 분석 메시지를 diary_analysis_messages에 저장
     */
    private void saveOceanAnalysisMessages(Integer userId, Integer diaryId, Map<String, Object> agentResponses) {
        log.info("OCEAN 분석 메시지 저장 - userId: {}, diaryId: {}", userId, diaryId);

        try {
            int messageOrder = 1;

            // Openness - 개방성
            String opennessMessage = (String) agentResponses.get("Openness");
            if (opennessMessage != null) {
                saveChatMessage(diaryId, OPENNESS_ACTOR_ID, opennessMessage, messageOrder++);
            }

            // Conscientiousness - 성실성
            String conscientiousnessMessage = (String) agentResponses.get("Conscientiousness");
            if (conscientiousnessMessage != null) {
                saveChatMessage(diaryId, CONSCIENTIOUSNESS_ACTOR_ID, conscientiousnessMessage, messageOrder++);
            }

            // Extroversion - 외향성 (AI 서버에서는 Extraversion으로 보내지만 올바른 철자는 Extroversion)
            String extroversionMessage = (String) agentResponses.get("Extraversion");
            if (extroversionMessage != null) {
                saveChatMessage(diaryId, EXTROVERSION_ACTOR_ID, extroversionMessage, messageOrder++);
            }

            // Agreeableness - 친화성
            String agreeablenessMessage = (String) agentResponses.get("Agreeableness");
            if (agreeablenessMessage != null) {
                saveChatMessage(diaryId, AGREEABLENESS_ACTOR_ID, agreeablenessMessage, messageOrder++);
            }

            // Neuroticism - 신경성
            String neuroticismMessage = (String) agentResponses.get("Neuroticism");
            if (neuroticismMessage != null) {
                saveChatMessage(diaryId, NEUROTICISM_ACTOR_ID, neuroticismMessage, messageOrder++);
            }

            log.info("OCEAN 분석 메시지 저장 완료 - diaryId: {}", diaryId);

        } catch (Exception e) {
            log.error("OCEAN 분석 메시지 저장 실패 - diaryId: {}, error: {}", diaryId, e.getMessage(), e);
            throw new RuntimeException("분석 메시지 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 개별 채팅 메시지 저장
     */
    private void saveChatMessage(Integer diaryId, Integer actorId, String message, Integer messageOrder) {
        DiaryAnalysisMessage chatMessage = DiaryAnalysisMessage.builder()
                .diaryId(diaryId)
                .senderActorId(actorId)
                .message(message)
                .messageOrder(messageOrder)
                .build();

        diaryAnalysisMessageRepository.save(chatMessage);
        log.debug("채팅 메시지 저장 - diaryId: {}, actorId: {}, order: {}, message: {}",
                diaryId, actorId, messageOrder, message.substring(0, Math.min(50, message.length())));
    }

    /**
     * DiaryAnalysisSummary 저장 (big5_scores, domain_classification, final_conclusion)
     */
    private void saveDiaryAnalysisSummary(Integer diaryId, Map<String, Object> analysisData) {
        try {
            log.info("DiaryAnalysisSummary 저장 시작 - diaryId: {}", diaryId);

            // 기존 요약 정보가 있는지 확인하고 삭제
            diaryAnalysisSummaryRepository.deleteByDiaryId(diaryId);

            // big5_scores 파싱 - OpenAI에서 재계산된 점수 사용
            Map<String, Double> big5Scores = new HashMap<>();
            Object big5DataObj = analysisData.get("big5_scores");

            if (big5DataObj instanceof Map) {
                Map<String, Object> big5Data = (Map<String, Object>) big5DataObj;
                log.debug("Big5 scores 원본: {}", big5Data);

                // OpenAI 응답의 키 매핑 (DB summary용)
                Map<String, String> keyMapping = new HashMap<>();
                keyMapping.put("개방성", "openness");
                keyMapping.put("성실성", "conscientiousness");
                keyMapping.put("외향성", "extroversion");
                keyMapping.put("친화성", "agreeableness");
                keyMapping.put("신경성", "neuroticism");
                // 영어 키도 지원 (fallback)
                keyMapping.put("openness", "openness");
                keyMapping.put("conscientiousness", "conscientiousness");
                keyMapping.put("extraversion", "extroversion");
                keyMapping.put("agreeableness", "agreeableness");
                keyMapping.put("neuroticism", "neuroticism");

                for (Map.Entry<String, Object> entry : big5Data.entrySet()) {
                    String originalKey = entry.getKey();
                    String mappedKey = keyMapping.getOrDefault(originalKey, keyMapping.getOrDefault(originalKey.toLowerCase(), originalKey));

                    if (entry.getValue() instanceof Number) {
                        big5Scores.put(mappedKey, ((Number) entry.getValue()).doubleValue());
                        log.debug("Big5 매핑: {} -> {} = {}", originalKey, mappedKey, entry.getValue());
                    }
                }
            } else {
                log.warn("big5_scores가 Map 타입이 아님: {}", big5DataObj != null ? big5DataObj.getClass().getSimpleName() : "null");
            }

            // domain_classification 파싱
            String domainClassification = (String) analysisData.get("domain_classification");

            // final_conclusion 파싱
            String finalConclusion = (String) analysisData.get("final_conclusion");

            // keywords 파싱
            List<String> keywords = new ArrayList<>();
            Object keywordsData = analysisData.get("keywords");
            if (keywordsData instanceof List) {
                List<?> keywordsList = (List<?>) keywordsData;
                for (Object keyword : keywordsList) {
                    if (keyword instanceof String) {
                        keywords.add((String) keyword);
                    }
                }
            }

            // DiaryAnalysisSummary 생성 및 저장
            DiaryAnalysisSummary summary = DiaryAnalysisSummary.builder()
                    .diaryId(diaryId)
                    .big5Scores(big5Scores)
                    .domainClassification(domainClassification)
                    .finalConclusion(finalConclusion)
                    .keywords(keywords)
                    .build();

            diaryAnalysisSummaryRepository.save(summary);

            log.info("DiaryAnalysisSummary 저장 완료 - diaryId: {}, domain: {}, big5Keys: {}, keywords: {}",
                    diaryId, domainClassification, big5Scores.keySet(), keywords);

        } catch (Exception e) {
            log.error("DiaryAnalysisSummary 저장 실패 - diaryId: {}, error: {}", diaryId, e.getMessage(), e);
            // 요약 정보 저장 실패해도 전체 프로세스는 계속 진행
        }
    }

    /**
     * 저장된 OCEAN 분석 메시지들 조회
     */
    public List<DiaryAnalysisMessage> getStoredAnalysisMessages(Integer userId, Integer diaryId) {
        try {
            log.info("저장된 OCEAN 분석 메시지 조회 - userId: {}, diaryId: {}", userId, diaryId);

            // 해당 다이어리의 모든 채팅 메시지 조회 (OCEAN 분석 결과 포함)
            List<DiaryAnalysisMessage> messages = diaryAnalysisMessageRepository.findByDiaryIdOrderByCreatedAtAsc(diaryId);

            log.info("OCEAN 분석 메시지 조회 완료 - diaryId: {}, count: {}", diaryId, messages.size());
            return messages;

        } catch (Exception e) {
            log.error("저장된 OCEAN 분석 메시지 조회 실패 - userId: {}, diaryId: {}, error: {}",
                    userId, diaryId, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 특정 다이어리에 분석 결과가 이미 저장되어 있는지 확인
     */
    public boolean hasAnalysisResults(Integer diaryId) {
        try {
            Long messageCount = diaryAnalysisMessageRepository.countByDiaryId(diaryId);
            return messageCount > 0;
        } catch (Exception e) {
            log.error("분석 결과 존재 여부 확인 실패 - diaryId: {}, error: {}", diaryId, e.getMessage());
            return false;
        }
    }

    /**
     * 다이어리 분석 요약 정보 조회
     */
    public DiaryAnalysisResponse.AnalysisSummary getAnalysisSummary(Integer diaryId) {
        try {
            log.info("다이어리 분석 요약 조회 - diaryId: {}", diaryId);

            DiaryAnalysisSummary summary = diaryAnalysisSummaryRepository.findByDiaryId(diaryId)
                    .orElse(null);

            if (summary == null) {
                log.warn("분석 요약 정보가 없음 - diaryId: {}", diaryId);
                return DiaryAnalysisResponse.AnalysisSummary.builder()
                        .big5Scores(new HashMap<>())
                        .domainClassification("UNKNOWN")
                        .finalConclusion("분석 결과를 불러올 수 없습니다.")
                        .keywords(new ArrayList<>())
                        .build();
            }

            return DiaryAnalysisResponse.AnalysisSummary.builder()
                    .big5Scores(summary.getBig5Scores() != null ? summary.getBig5Scores() : new HashMap<>())
                    .domainClassification(summary.getDomainClassification())
                    .finalConclusion(summary.getFinalConclusion())
                    .keywords(summary.getKeywords() != null ? summary.getKeywords() : new ArrayList<>())
                    .build();

        } catch (Exception e) {
            log.error("분석 요약 조회 실패 - diaryId: {}, error: {}", diaryId, e.getMessage(), e);
            // 실패 시 기본값 반환
            return DiaryAnalysisResponse.AnalysisSummary.builder()
                    .big5Scores(new HashMap<>())
                    .domainClassification("ERROR")
                    .finalConclusion("분석 결과 조회 중 오류가 발생했습니다.")
                    .keywords(new ArrayList<>())
                    .build();
        }
    }

}