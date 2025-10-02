from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Dict, List, Optional
import asyncio
import logging
import uvicorn
from contextlib import asynccontextmanager

from reasoning_persona_system import ReasoningPersonaSystem
from bert_emotion_detector import BERTEmotionDetector

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

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
        logger.info("✅ AI 시스템 초기화 완료")
    except Exception as e:
        logger.error(f"❌ AI 시스템 초기화 실패: {e}")
        raise
    
    yield
    
    logger.info("🔄 AI 시스템 정리 완료")

# FastAPI 앱 생성
app = FastAPI(
    title="MyOcean AI Server",
    description="Big5 성격 분석 및 AI 상담 서비스",
    version="1.0.0",
    lifespan=lifespan
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# === Request/Response 모델 ===

class DiaryAnalysisRequest(BaseModel):
    user_id: int
    diary_id: int
    content: str
    title: Optional[str] = None

class ChatRequest(BaseModel):
    user_id: int
    message: str
    chat_type: str  # "my", "diary"
    diary_id: Optional[int] = None
    big5_scores: Optional[Dict[str, float]] = None

class Big5ScoresResponse(BaseModel):
    openness: float      # O
    conscientiousness: float  # C  
    extraversion: float  # E
    agreeableness: float # A
    neuroticism: float   # N

class DiaryAnalysisResponse(BaseModel):
    success: bool
    big5_scores: Big5ScoresResponse
    domain_classification: str
    final_conclusion: str
    recommendations: List[str]
    agent_responses: Dict[str, str]

class ChatResponse(BaseModel):
    success: bool
    message: str
    agent_type: str
    big5_scores: Big5ScoresResponse

# === API 엔드포인트 ===

@app.get("/health")
async def health_check():
    """서버 상태 확인"""
    return {
        "status": "healthy",
        "reasoning_system": reasoning_system is not None,
        "bert_detector": bert_detector is not None
    }

@app.post("/ai/analyze/personality", response_model=Big5ScoresResponse)
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

@app.post("/ai/analyze/diary", response_model=DiaryAnalysisResponse)
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

@app.post("/ai/chat", response_model=ChatResponse)
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

def extract_recommendations(conclusion: str) -> List[str]:
    """결론에서 추천사항 추출"""
    recommendations = []
    sentences = conclusion.split('.')
    
    for sentence in sentences:
        sentence = sentence.strip()
        if any(keyword in sentence for keyword in ['추천', '제안', '시도', '해보세요', '권장']):
            recommendations.append(sentence)
    
    return recommendations[:3]

if __name__ == "__main__":
    uvicorn.run(
        "ai_server:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )