from pydantic import BaseModel
from typing import Dict, List


class Big5ScoresResponse(BaseModel):
    """Big5 성격 점수 응답"""
    openness: float      # O
    conscientiousness: float  # C
    extraversion: float  # E
    agreeableness: float # A
    neuroticism: float   # N


class DiaryAnalysisResponse(BaseModel):
    """다이어리 분석 응답"""
    success: bool
    big5_scores: Big5ScoresResponse
    domain_classification: str
    final_conclusion: str
    recommendations: List[str]
    agent_responses: Dict[str, str]


class ChatResponse(BaseModel):
    """AI 채팅 응답"""
    success: bool
    message: str
    agent_type: str
    big5_scores: Big5ScoresResponse
