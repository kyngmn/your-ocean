from fastapi import APIRouter, HTTPException
import logging

from app.schemas.request import DiaryAnalysisRequest
from app.schemas.response import DiaryAnalysisResponse, Big5ScoresResponse
from app.services.diary_service import extract_recommendations

logger = logging.getLogger(__name__)
router = APIRouter()

# 전역 시스템들은 main.py에서 주입받음
reasoning_system = None
bert_detector = None


def set_ai_systems(reasoning_sys, bert_det):
    """AI 시스템 설정"""
    global reasoning_system, bert_detector
    reasoning_system = reasoning_sys
    bert_detector = bert_det


@router.post("/analyze/diary", response_model=DiaryAnalysisResponse)
async def analyze_diary(request: DiaryAnalysisRequest):
    """다이어리 분석 및 AI 상담"""
    try:
        if not reasoning_system or not bert_detector:
            raise HTTPException(status_code=503, detail="AI 시스템이 초기화되지 않았습니다")

        logger.info(f"📝 다이어리 분석 시작 - User: {request.user_id}, Diary: {request.diary_id}")

        # 1. BERT로 Big5 점수 추출
        bert_scores = bert_detector.predict(request.content)

        # 2. ReasoningPersonaSystem으로 종합 분석
        analysis_result = await reasoning_system.process_conversation(
            request.content,
            bert_scores
        )

        # 3. 응답 생성
        big5_response = Big5ScoresResponse(
            openness=bert_scores.get("Openness", 0.5),
            conscientiousness=bert_scores.get("Conscientiousness", 0.5),
            extraversion=bert_scores.get("Extraversion", 0.5),
            agreeableness=bert_scores.get("Agreeableness", 0.5),
            neuroticism=bert_scores.get("Neuroticism", 0.5)
        )

        response = DiaryAnalysisResponse(
            success=True,
            big5_scores=big5_response,
            domain_classification=analysis_result.get("domain_classification", "GENERAL"),
            final_conclusion=analysis_result.get("final_conclusion", ""),
            recommendations=extract_recommendations(analysis_result.get("final_conclusion", "")),
            agent_responses=analysis_result.get("agent_responses", {})
        )

        logger.info(f"✅ 다이어리 분석 완료 - User: {request.user_id}")
        return response

    except Exception as e:
        logger.error(f"❌ 다이어리 분석 오류: {e}")
        raise HTTPException(status_code=500, detail=f"다이어리 분석 실패: {str(e)}")
