# 🎭 BERT 기반 감정 분석 데모

Hugging Face의 `Minej/bert-base-personality` 모델을 사용한 텍스트 감정 분석 애플리케이션입니다.

## 🚀 빠른 시작

### Conda 환경 사용 (권장)

1. **환경 설정**
```bash
# 실행 권한 부여
chmod +x setup.sh

# 환경 설정 실행
./setup.sh
```

2. **환경 활성화**
```bash
conda activate bert-emotion
```

3. **데모 실행**
```bash
streamlit run streamlit_demo.py
```

### Docker 사용

1. **Docker 빌드 및 실행**
```bash
docker-compose up --build
```

2. **브라우저에서 접속**
```
http://localhost:8501
```

## 📁 프로젝트 구조

```
├── bert_emotion_detector.py  # BERT 감정 분석 모델 클래스
├── streamlit_demo.py        # Streamlit 웹 데모
├── environment.yml          # Conda 환경 설정
├── requirements.txt         # Python 의존성
├── setup.sh                # 환경 설정 스크립트
├── Dockerfile              # Docker 이미지 설정
├── docker-compose.yml      # Docker Compose 설정
└── README.md               # 이 문서
```

## 🎯 주요 기능

- **단일 텍스트 분석**: 입력한 텍스트의 감정을 실시간으로 분석
- **배치 분석**: 여러 텍스트를 한 번에 분석
- **시각화**: 막대 차트, 파이 차트로 결과 시각화
- **예시 텍스트**: 미리 준비된 예시로 빠른 테스트
- **반응형 UI**: 모바일과 데스크톱 모두 지원

## 🛠️ 사용법

### 1. 단일 텍스트 분석
- 텍스트 입력란에 분석할 문장 입력
- "감정 분석" 버튼 클릭
- 결과 및 시각화 확인

### 2. 배치 분석
- "배치 분석" 섹션 확장
- 여러 문장을 한 줄에 하나씩 입력
- "배치 분석" 버튼 클릭

## 🔧 개발 환경 설정

### 수동 설치

```bash
# 가상환경 생성
conda create -n bert-emotion python=3.9
conda activate bert-emotion

# 의존성 설치
pip install -r requirements.txt
```

### 모델 테스트

```bash
python bert_emotion_detector.py
```

## 📊 모델 정보

- **모델**: Minej/bert-base-personality
- **기반**: BERT
- **용도**: 텍스트 감정/성격 분석
- **언어**: 한국어/영어 지원

## 🚨 문제 해결

### 모델 로딩 오류
```bash
# 캐시 정리
rm -rf ~/.cache/huggingface/transformers/
```

### CUDA 메모리 부족
- CPU 모드로 자동 전환됩니다
- GPU 사용 시 배치 크기를 줄여보세요

### Streamlit 포트 충돌
```bash
streamlit run streamlit_demo.py --server.port 8502
```

## 🤝 기여

1. Fork 이 프로젝트
2. Feature 브랜치 생성
3. 변경사항 커밋
4. Pull Request 생성

## 📝 라이센스

MIT License