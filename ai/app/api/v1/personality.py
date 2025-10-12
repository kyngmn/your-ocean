from fastapi import APIRouter, HTTPException
import logging

from app.schemas.response import Big5ScoresResponse

logger = logging.getLogger(__name__)
router = APIRouter()

# 전역 BERT detector는 main.py에서 주입받음
bert_detector = None


def set_bert_detector(detector):
    """BERT detector 설정"""
    global bert_detector
    bert_detector = detector


@router.post("/analyze/personality", response_model=Big5ScoresResponse)
async def analyze_personality(text: str):
    """텍스트에서 Big5 성격 점수 추출"""
    try:
        if not bert_detector:
            raise HTTPException(status_code=503, detail="BERT 모델이 초기화되지 않았습니다")

        logger.info(f"🧠 BERT 성격 분석: {text[:50]}...")
        scores = bert_detector.predict(text)

        return Big5ScoresResponse(
            openness=scores.get("Openness", 0.5),
            conscientiousness=scores.get("Conscientiousness", 0.5),
            extraversion=scores.get("Extraversion", 0.5),
            agreeableness=scores.get("Agreeableness", 0.5),
            neuroticism=scores.get("Neuroticism", 0.5)
        )

    except Exception as e:
        logger.error(f"❌ 성격 분석 오류: {e}")
        raise HTTPException(status_code=500, detail=f"성격 분석 실패: {str(e)}")
