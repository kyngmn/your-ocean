# 🌊 MyOcean AI Server

Big5 성격 분석 기반 AI 시스템

## 📖 프로젝트 소개

사용자의 텍스트(다이어리, 채팅)를 분석하여 Big5 성격 특성을 파악하고,
LangGraph 기반의 다중 에이전트 시스템이 협력하여 개인 맞춤형 대화를 제공합니다.

### 🎯 주요 기능

- **🧠 BERT 성격 분석**: Hugging Face BERT 모델로 실시간 Big5 성격 점수 추출
- **🤖 Multi-Agent 상담**: 5개의 Big5 전문 에이전트가 협력하여 종합 상담
- **📝 다이어리 분석**: 작성한 다이어리를 분석하여 성격 특성과 조언 제공
- **💬 AI 채팅**: 실시간 대화를 통한 맞춤형 상담

### 🧩 핵심 기술 스택

- **FastAPI**: REST API 서버
- **LangGraph**: Multi-agent 워크플로우
- **LangChain**: LLM 체인 구성
- **BERT (Transformers)**: 성격 분석 모델
- **OpenAI GPT**: 대화 생성

---

## 📁 폴더 구조

```
ai/
├── main.py                          # 🚀 FastAPI 앱 진입점
├── requirements.txt                 # 📦 의존성 관리
│
├── app/                             # FastAPI 애플리케이션
│   ├── api/                         # API 라우터
│   │   └── v1/
│   │       ├── personality.py       # GET  /ai/analyze/personality (성격 분석)
│   │       ├── diary.py             # POST /ai/analyze/diary (다이어리 분석)
│   │       └── chat.py              # POST /ai/chat (AI 채팅)
│   │
│   ├── core/                        # 설정 및 상수
│   │   └── config.py                # CORS, 로깅 설정
│   │
│   ├── schemas/                     # Pydantic 스키마 (DTO)
│   │   ├── request.py               # Request 모델
│   │   └── response.py              # Response 모델
│   │
│   ├── services/                    # 비즈니스 로직
│   │   └── diary_service.py         # 다이어리 관련 서비스
│   │
│   └── models/                      # ML 모델
│       └── bert_emotion.py          # BERT 성격 분석 모델
│
├── systems/                         # LangGraph 워크플로우
│   ├── reasoning_system.py          # 추론 페르소나 시스템
│   └── enhanced_system.py           # 강화된 LangGraph 시스템
│
├── clients/                         # 외부 API 클라이언트
│   └── gms_client.py                # OpenAI API 클라이언트
│
├── agents/                          # LangGraph 에이전트 (미래 확장용)
│
├── tests/                           # 테스트
│   └── test_api_simple.py
│
└── demos/                           # 데모 및 예시
    ├── streamlit_reasoning_demo.py  # Streamlit 데모
    └── simple_debug_streamlit.py
```

### 📂 폴더별 역할

| 폴더 | 역할 | Spring 비유 |
|------|------|------------|
| `app/api/` | REST API 엔드포인트 | `@RestController` |
| `app/services/` | 비즈니스 로직 | `@Service` |
| `app/schemas/` | Request/Response 모델 | DTO 클래스 |
| `app/models/` | ML 모델 클래스 | - |
| `app/core/` | 설정 및 상수 | `application.yml`, `@Configuration` |
| `systems/` | LangGraph 워크플로우 | - (AI 전용) |
| `clients/` | 외부 API 클라이언트 | `@FeignClient` |

---

## 🔄 시스템 흐름

### 1️⃣ 다이어리 분석 플로우

```
사용자 다이어리 입력
    ↓
[POST /ai/analyze/diary] (diary.py)
    ↓
BERT 모델 → Big5 성격 점수 추출 (bert_emotion.py)
    ↓
ReasoningPersonaSystem 호출 (reasoning_system.py)
    ↓
├─ Domain Agent: 도메인 분류 (EXTRAVERSION, AGREEABLENESS 등)
├─ Big5 Agents: 5개 에이전트가 순차적으로 분석
│   ├─ 외향성 Agent 🌟
│   ├─ 친화성 Agent 🤝
│   ├─ 성실성 Agent 📋
│   ├─ 신경성 Agent 🤔
│   └─ 개방성 Agent 🎨
└─ Synthesis Node: GPT로 종합 결론 생성
    ↓
DiaryAnalysisResponse 반환
    ├─ big5_scores: 성격 점수
    ├─ domain_classification: 분류된 도메인
    ├─ final_conclusion: 종합 결론
    ├─ recommendations: 추천사항
    └─ agent_responses: 각 에이전트 응답
```

### 2️⃣ AI 채팅 플로우

```
사용자 메시지 입력
    ↓
[POST /ai/chat] (chat.py)
    ↓
BERT 모델 → Big5 성격 점수 (또는 기존 점수 사용)
    ↓
ReasoningPersonaSystem → Multi-agent 토론
    ↓
ChatResponse 반환
    ├─ message: AI 응답
    ├─ agent_type: 활성화된 에이전트 타입
    └─ big5_scores: 성격 점수
```

### 3️⃣ LangGraph 내부 동작 (systems/reasoning_system.py)

```
StateGraph 워크플로우:

START
  ↓
classify_domain (도메인 분류)
  ↓
extraversion_agent (외향성 분석)
  ↓
agreeableness_agent (친화성 분석)
  ↓
conscientiousness_agent (성실성 분석)
  ↓
neuroticism_agent (신경성 분석)
  ↓
openness_agent (개방성 분석)
  ↓
final_synthesis (GPT로 종합)
  ↓
END
```

---

## 🚀 빠른 시작

### 1️⃣ 환경 설정

```bash
# Python 3.9+ 필요
python --version

# 가상환경 생성 (선택)
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
```

### 2️⃣ 의존성 설치

```bash
pip install -r requirements.txt
```

### 3️⃣ 환경변수 설정

`.env` 파일 생성:

```bash
OPENAI_API_KEY=your_openai_api_key_here
```

### 4️⃣ 서버 실행

```bash
# 방법 1: 직접 실행
python main.py

# 방법 2: uvicorn 사용
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

서버가 실행되면: `http://localhost:8000`

---

## 📡 API 엔드포인트

### 🏥 Health Check

```bash
GET /health
```

**Response:**
```json
{
  "status": "healthy",
  "reasoning_system": true,
  "bert_detector": true
}
```

---

### 🧠 성격 분석

```bash
POST /ai/analyze/personality?text=오늘 친구들과 즐거운 시간을 보냈어요
```

**Response:**
```json
{
  "openness": 0.75,
  "conscientiousness": 0.62,
  "extraversion": 0.88,
  "agreeableness": 0.70,
  "neuroticism": 0.35
}
```

---

### 📝 다이어리 분석

```bash
POST /ai/analyze/diary
Content-Type: application/json

{
  "user_id": 123,
  "diary_id": 456,
  "content": "오늘 새로운 프로젝트를 시작했다. 혼자 하는 게 편하지만 팀원들과 협력해야 해서 고민이다.",
  "title": "새 프로젝트 시작"
}
```

**Response:**
```json
{
  "success": true,
  "big5_scores": {
    "openness": 0.70,
    "conscientiousness": 0.85,
    "extraversion": 0.40,
    "agreeableness": 0.55,
    "neuroticism": 0.60
  },
  "domain_classification": "CONSCIENTIOUSNESS",
  "final_conclusion": "당신은 계획적이고 목표 지향적인 성향이 강합니다...",
  "recommendations": [
    "팀원들과의 소통 방식을 미리 계획해보세요",
    "혼자만의 작업 시간을 확보하는 것도 중요합니다"
  ],
  "agent_responses": {
    "Extraversion": "...",
    "Conscientiousness": "...",
    ...
  }
}
```

---

### 💬 AI 채팅

```bash
POST /ai/chat
Content-Type: application/json

{
  "user_id": 123,
  "message": "요즘 스트레스가 심해요",
  "chat_type": "my",
  "big5_scores": {
    "openness": 0.7,
    "conscientiousness": 0.6,
    "extraversion": 0.5,
    "agreeableness": 0.8,
    "neuroticism": 0.7
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "신경성 점수가 높은 편이시네요. 스트레스 관리를 위해...",
  "agent_type": "NEUROTICISM",
  "big5_scores": { ... }
}
```

---

## 🧪 테스트

```bash
# 간단한 API 테스트
python tests/test_api_simple.py

# Streamlit 데모 실행
streamlit run demos/streamlit_reasoning_demo.py
```

---

## 🐳 Docker 배포 (프로덕션)

```bash
# Docker Compose로 실행
docker-compose up --build

# AI 전용 컨테이너
docker-compose -f docker-compose.ai.yml up --build
```

---

## 🛠️ 개발 가이드

### 새 API 엔드포인트 추가하기

1. **스키마 정의**: `app/schemas/request.py`, `response.py`
2. **라우터 생성**: `app/api/v1/your_feature.py`
3. **서비스 로직**: `app/services/your_service.py`
4. **main.py에 등록**:
   ```python
   from app.api.v1 import your_feature
   app.include_router(your_feature.router, prefix="/ai", tags=["YourFeature"])
   ```

### 새 LangGraph 시스템 추가하기

`systems/` 폴더에 새로운 워크플로우 파일 생성:
```python
from langgraph.graph import StateGraph, END

class YourSystem:
    def _build_graph(self):
        workflow = StateGraph(...)
        # 노드 및 엣지 추가
        return workflow.compile()
```

---

## 🔧 트러블슈팅

### BERT 모델 로딩 오류
```bash
# 캐시 정리
rm -rf ~/.cache/huggingface/transformers/
python -c "from app.models.bert_emotion import BERTEmotionDetector; BERTEmotionDetector()"
```

### OpenAI API 오류
```bash
# .env 파일 확인
cat .env

# API 키 테스트
python -c "from clients.gms_client import GMSClient; GMSClient().simple_chat('test')"
```

### 포트 충돌
```bash
# 다른 포트로 실행
uvicorn main:app --port 8001
```

---

## 📚 참고 자료

- [FastAPI 공식 문서](https://fastapi.tiangolo.com/)
- [LangGraph 가이드](https://python.langchain.com/docs/langgraph)
- [BERT 모델 (Hugging Face)](https://huggingface.co/Minej/bert-base-personality)

