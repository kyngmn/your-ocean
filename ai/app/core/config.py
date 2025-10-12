import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


# CORS 설정
CORS_CONFIG = {
    "allow_origins": ["*"],
    "allow_credentials": True,
    "allow_methods": ["*"],
    "allow_headers": ["*"],
}


# FastAPI 앱 메타데이터
APP_CONFIG = {
    "title": "MyOcean AI Server",
    "description": "Big5 성격 분석 및 AI 상담 서비스",
    "version": "1.0.0",
}
