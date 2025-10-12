# 🤖 Big5 Agent AI 시스템

BERT 성격 분석 + GPT-5-mini를 결합한 개인화 AI 상담 시스템입니다.

## ✨ 주요 기능

- **🧠 BERT 성격 분석**: 실시간 Big5 성격 특성 분석
- **🤖 5개 전문 Agent**: 각 성격 특성별 전문 상담사
- **📊 개인화 학습**: 사용자별 패턴 학습 및 변화 감지
- **💬 자연스러운 대화**: GPT-5-mini 기반 맞춤형 응답
- **📈 인사이트 분석**: 성격 변화 추이 및 대화 패턴 분석

## 🚀 빠른 시작

### 1. 환경 설정

```bash
# 저장소 클론
git clone <repository-url>
cd ai

# 가상환경 생성 (conda 권장)
conda create -n agent-ai python=3.9
conda activate agent-ai

# 패키지 설치
pip install -r requirements.txt
```

### 2. API 키 설정

```bash
# .env 파일 생성
cp .env.example .env

# .env 파일 편집하여 GMS API 키 설정
# GMS_KEY=your_actual_gms_api_key_here
```

### 3. 실행

```bash
# Agent AI 데모 실행
streamlit run agent_ai_demo.py

# 또는 개별 컴포넌트 테스트
python gms_client.py          # GMS API 테스트
python bert_emotion_detector.py  # BERT 모델 테스트
python domain_agent.py        # 도메인 분류 테스트
python big5_agents.py         # Big5 Agent 테스트
python agent_ai_system.py     # 전체 시스템 테스트
```

## 🏗️ 시스템 아키텍처

```
사용자 입력
    ↓
BERT 성격 분석 → Domain Agent (라우터) → Big5 전문 Agents
    ↓                ↓                     ↓
개인화 학습 ← GPT-5-mini API ← 맞춤형 상담 응답
    ↓
패턴 변화 감지 & 후속 질문 유도
```

### 핵심 컴포넌트

1. **GMSClient**: GPT-5-mini API 연동
2. **BERTEmotionDetector**: 실시간 성격 분석
3. **DomainAgent**: 입력을 적절한 전문 Agent로 라우팅
4. **Big5Agents**: 5개 성격 특성별 전문 상담사
   - 🌟 **외향성 Agent**: 사회적 상호작용, 활동성
   - 🤝 **친화성 Agent**: 대인관계, 협력, 공감
   - 📋 **성실성 Agent**: 목표달성, 계획성
   - 😰 **신경성 Agent**: 감정관리, 스트레스
   - 🎨 **개방성 Agent**: 창의성, 새로운 경험

## 🎯 사용 예시

### 기본 사용법

```python
from agent_ai_system import AgentAISystem

# 시스템 초기화
agent_ai = AgentAISystem()

# AI와 대화
response = agent_ai.chat("요즘 친구들과 만나는 게 피곤해요", user_id="user123")
print(response)

# 사용자 인사이트 분석
insights = agent_ai.get_user_insights("user123")
print(insights)
```

### 고급 사용법

```python
# 상세 분석 결과 받기
result = agent_ai.process_user_input("스트레스가 너무 심해요", "user123")
print(f"선택된 Agent: {result['selected_agent']}")
print(f"성격 분석: {result['personality_analysis']}")
print(f"AI 응답: {result['agent_response']['response']}")
```

## 📊 특별한 기능들

### 🧠 개인화 학습
- 사용자별 성격 패턴 학습
- 대화 히스토리 기반 맞춤형 응답
- 성격 변화 추이 모니터링

### 🔍 패턴 변화 감지
```python
# 평소와 다른 패턴 감지시 자동으로 탐색 질문 생성
# "평소와 좀 다른 것 같은데, 최근에 특별한 일이 있었나요?"
```

### 📈 실시간 인사이트
- 성격 변화 추세 분석
- 상담 영역별 이용 빈도
- 대화 패턴 (시간대, 스타일) 분석

## 🔧 설정 및 환경변수

### 필수 환경변수
```bash
GMS_KEY=your_gms_api_key_here
```

### 선택적 설정
```python
# 커스텀 BERT 모델 사용
detector = BERTEmotionDetector("custom-model-name")

# API 설정 조정
gms_client = GMSClient(api_key="custom_key")
```

## 📱 Streamlit 데모 기능

### 💬 AI 상담 탭
- 실시간 채팅 인터페이스
- 빠른 질문 버튼
- Agent별 응답 표시

### 📊 성격 분석 탭
- 실시간 성격 레이더 차트
- 성격 변화 추이 그래프
- 상세 점수 테이블

### 🔍 인사이트 탭
- 개인화 분석 결과
- 상담 영역별 빈도
- 대화 패턴 분석

## 🚨 주의사항

### 보안
- `.env` 파일을 git에 커밋하지 마세요
- API 키를 코드에 직접 입력하지 마세요
- 개인정보는 로컬에만 저장됩니다

### 성능
- 첫 실행시 BERT 모델 다운로드로 시간 소요
- GPU 사용시 더 빠른 성격 분석 가능
- API 호출 비용 고려하여 사용

## 🤝 기여하기

1. Fork 프로젝트
2. Feature 브랜치 생성 (`git checkout -b feature/AmazingFeature`)
3. 변경사항 커밋 (`git commit -m 'Add some AmazingFeature'`)
4. 브랜치 Push (`git push origin feature/AmazingFeature`)
5. Pull Request 생성

## 📝 라이센스

MIT License

## 🆘 문제 해결

### 자주 발생하는 오류

**GMS API 오류**
```bash
# .env 파일 확인
cat .env

# API 키 유효성 테스트
python gms_client.py
```

**BERT 모델 로딩 오류**
```bash
# 캐시 정리
rm -rf ~/.cache/huggingface/

# 모델 재다운로드
python bert_emotion_detector.py
```

**Streamlit 실행 오류**
```bash
# 패키지 재설치
pip install -r requirements.txt --force-reinstall

# 포트 변경
streamlit run agent_ai_demo.py --server.port 8502
```

## 📞 연락처

프로젝트에 대한 질문이나 제안이 있으시면 Issues를 통해 연락해주세요!