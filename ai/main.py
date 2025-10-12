from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import uvicorn

from app.core.config import logger, CORS_CONFIG, APP_CONFIG
from app.api.v1 import personality, diary, chat
from systems.reasoning_system import ReasoningPersonaSystem
from app.models.bert_emotion import BERTEmotionDetector

# 전역 AI 시스템
reasoning_system = None
bert_detector = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """앱 시작/종료 이벤트"""
    global reasoning_system, bert_detector

    logger.info("🚀 AI 시스템 초기화 중...")
    try:
        bert_detector = BERTEmotionDetector()
        reasoning_system = ReasoningPersonaSystem()

        # API 라우터에 AI 시스템 주입
        personality.set_bert_detector(bert_detector)
        diary.set_ai_systems(reasoning_system, bert_detector)
        chat.set_ai_systems(reasoning_system, bert_detector)

        logger.info("✅ AI 시스템 초기화 완료")
    except Exception as e:
        logger.error(f"❌ AI 시스템 초기화 실패: {e}")
        raise

    yield

    logger.info("🔄 AI 시스템 정리 완료")


# FastAPI 앱 생성
app = FastAPI(
    title=APP_CONFIG["title"],
    description=APP_CONFIG["description"],
    version=APP_CONFIG["version"],
    lifespan=lifespan
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    **CORS_CONFIG
)

# API 라우터 등록
app.include_router(personality.router, prefix="/ai", tags=["Personality"])
app.include_router(diary.router, prefix="/ai", tags=["Diary"])
app.include_router(chat.router, prefix="/ai", tags=["Chat"])


@app.get("/health")
async def health_check():
    """서버 상태 확인"""
    return {
        "status": "healthy",
        "reasoning_system": reasoning_system is not None,
        "bert_detector": bert_detector is not None
    }


if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )
