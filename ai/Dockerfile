# Big5 추론 페르소나 시스템 Docker 이미지
FROM python:3.12-slim

# 작업 디렉토리 설정
WORKDIR /app

# 시스템 의존성 설치 (curl 추가)
RUN apt-get update && apt-get install -y \
    git \
    curl \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Python 의존성 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# 애플리케이션 파일 복사
COPY . .

# .env 파일이 없으면 템플릿 생성
RUN if [ ! -f .env ]; then \
    echo "# GMS API 키를 여기에 입력하세요" > .env && \
    echo "GMS_KEY=your_api_key_here" >> .env; \
    fi

# Streamlit 포트 노출
EXPOSE 8501

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl --fail http://localhost:8501/_stcore/health || exit 1

# Streamlit 실행 (프로덕션 설정)
CMD ["streamlit", "run", "streamlit_reasoning_demo.py", \
     "--server.port=8501", \
     "--server.address=0.0.0.0", \
     "--server.headless=true", \
     "--server.fileWatcherType=none", \
     "--browser.gatherUsageStats=false"]