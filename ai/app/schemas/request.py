from pydantic import BaseModel
from typing import Dict, Optional


class DiaryAnalysisRequest(BaseModel):
    """다이어리 분석 요청"""
    user_id: int
    diary_id: int
    content: str
    title: Optional[str] = None


class ChatRequest(BaseModel):
    """AI 채팅 요청"""
    user_id: int
    message: str
    chat_type: str  # "my", "diary"
    diary_id: Optional[int] = None
    big5_scores: Optional[Dict[str, float]] = None
