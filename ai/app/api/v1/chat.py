from fastapi import APIRouter, HTTPException
import logging

from app.schemas.request import ChatRequest
from app.schemas.response import ChatResponse, Big5ScoresResponse

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


@router.post("/chat", response_model=ChatResponse)
async def chat_with_ai(request: ChatRequest):
    """AI와 채팅"""
    try:
        if not reasoning_system or not bert_detector:
            raise HTTPException(status_code=503, detail="AI 시스템이 초기화되지 않았습니다")

        logger.info(f"💬 AI 채팅 시작 - User: {request.user_id}, Type: {request.chat_type}")

        # Big5 점수 준비
        if request.big5_scores:
            big5_scores = {
                "Openness": request.big5_scores.get("openness", 0.5),
                "Conscientiousness": request.big5_scores.get("conscientiousness", 0.5),
                "Extraversion": request.big5_scores.get("extraversion", 0.5),
                "Agreeableness": request.big5_scores.get("agreeableness", 0.5),
                "Neuroticism": request.big5_scores.get("neuroticism", 0.5)
            }
        else:
            big5_scores = bert_detector.predict(request.message)

        # AI 응답 생성
        analysis_result = await reasoning_system.process_conversation(
            request.message,
            big5_scores
        )

        response = ChatResponse(
            success=True,
            message=analysis_result.get("final_conclusion", "죄송합니다. 응답을 생성할 수 없습니다."),
            agent_type=analysis_result.get("domain_classification", "GENERAL"),
            big5_scores=Big5ScoresResponse(
                openness=big5_scores.get("Openness", 0.5),
                conscientiousness=big5_scores.get("Conscientiousness", 0.5),
                extraversion=big5_scores.get("Extraversion", 0.5),
                agreeableness=big5_scores.get("Agreeableness", 0.5),
                neuroticism=big5_scores.get("Neuroticism", 0.5)
            )
        )

        logger.info(f"✅ AI 채팅 완료 - User: {request.user_id}")
        return response

    except Exception as e:
        logger.error(f"❌ AI 채팅 오류: {e}")
        raise HTTPException(status_code=500, detail=f"AI 채팅 실패: {str(e)}")
