# 나의 OCEAN은

<div align="center">

**사용자의 내면 성격을 '페르소나 세포들'로 시각화하여,**
**성격을 다각도로 탐험하고 성장하는 자아 탐색 플랫폼**

[![Service](https://img.shields.io/badge/Service-myocean.cloud-blue)](https://myocean.cloud/)
[![API Docs](https://img.shields.io/badge/API-Swagger-green)](https://be.myocean.cloud/swagger-ui/index.html)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [핵심 특징](#핵심-특징)
- [주요 화면](#주요-화면)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [프로젝트 구조](#프로젝트-구조)
- [개발 가이드](#개발-가이드)
- [팀원 소개](#팀원-소개)
- [관련 문서](#관련-문서)

---

## 프로젝트 소개

### 🎬 소개 영상

<div align="center">
  <a href="https://youtu.be/ETfpIRG7VT0" target="_blank">
    <img src="https://img.youtube.com/vi/ETfpIRG7VT0/maxresdefault.jpg" alt="나의 OCEAN은 프로젝트 소개 영상" width="80%">
  </a>
  <p><em>나의 OCEAN은 프로젝트 소개 영상 - <a href="https://youtu.be/ETfpIRG7VT0" target="_blank">YouTube에서 보기</a></em></p>
</div>

---

### 개요

'**유미의 세포들**'과 '**인사이드 아웃**'에서 영감을 받아 탄생한 **나의 OCEAN은**은 Big Five 성격 이론(OCEAN)을 기반으로 사용자의 내면을 5가지 페르소나 세포로 시각화하는 혁신적인 자아 탐색 플랫폼입니다.

### 배경 및 목적

기존의 성격 검사는 단순히 결과를 보여주는 데 그치며, 자기보고식 검사의 한계로 인해 주관적인 편향이 발생할 수 있습니다. **나의 OCEAN은**은 이러한 한계를 극복하기 위해:

- **행동 기반 데이터**와 **자기보고식 검사**를 결합한 하이브리드 분석
- **AI 에이전트**와의 실시간 대화를 통한 몰입형 자기 성찰
- **지속적인 성격 분석**을 통한 자아 성장 지원

를 제공합니다.

### 프로젝트 비전

단순한 성격 검사 도구를 넘어, 사용자가 자신의 내면을 탐험하고, '내가 생각하는 나'와 '실제 나'의 차이를 발견하며, 지속적으로 성장할 수 있는 **자아 성찰의 동반자**가 되는 것을 목표로 합니다.

---

## 핵심 특징

### 1. 행동 기반 성격 측정

#### 자기보고식 한계 극복
기존 검사의 주관적 편향을 극복하기 위해 게임 내 행동 데이터를 분석합니다:
- 선택의 패턴 분석
- 반응 시간 측정
- 의사결정 경향 추적
- 스트레스 상황에서의 행동 관찰

#### 간극 시각화
- 자기 진단 결과와 행동 기반 분석을 비교
- '내가 생각하는 나' vs '실제 나'의 차이를 시각적으로 표현
- 5대 성격 요인별 간극을 직관적인 차트로 제공

### 2. 동적 자아 AI 집단

#### 통합적 대화 시스템
- 5대 성격 척도별 AI 페르소나가 사용자의 일기/행동 분석 결과를 바탕으로 '내면의 토론' 진행
- 각 페르소나는 독립적인 관점에서 사용자의 상황을 분석하고 조언 제공
- Multi-Agent 시스템을 통한 다각도 인사이트 생성

#### 몰입감 있는 경험
- 정적인 분석 리포트가 아닌, 살아있는 AI와의 실제 대화
- 페르소나 세포들 간의 회의 시뮬레이션
- 사용자 맞춤형 피드백 및 성장 제안

### 3. 종합적 분석

#### 다층적 성격 분석
- **자기보고식 검사**: 사용자가 인식하는 자신의 성격
- **행동 기반 분석**: 게임 플레이를 통해 드러나는 실제 성향
- **간극 시각화**: '내가 생각하는 나' vs '실제 나'의 차이 발견

#### 시간 추적 기능
- 일기 작성을 통한 자기 성찰 기록
- 성격 특성의 변화 추이 모니터링
- 장기적인 성장 기록 및 회고

---

## 주요 화면

### 핵심 기능 화면

#### 1. 홈 & 대시보드
처음 방문하는 사용자와 페르소나를 생성한 사용자를 위한 맞춤형 홈 화면


| <img src="./docs/산출물/홈_페르소나존재X.png" alt="페르소나존재X" width="300"> | <img src="./docs/산출물/홈_페르소나존재.png" alt="페르소나존재" width="477"> |
|:--:|:--:|
| 처음 방문 시 | 페르소나 생성 후 |


#### 2. Big5 성격 검사
사용자의 성격을 OCEAN 5요인으로 분석하는 온보딩 프로세스

| <img src="./docs/산출물/BIG5_온보딩.png" width="420" alt="Big5 온보딩"> | <img src="./docs/산출물/BIG5_설문진행.png" alt="Big5 설문" width="300"> |
|:--:|:--:|
| 온보딩 시작 | 설문 진행 |


#### 3. 나의 페르소나
5가지 성격 요인별 페르소나 세포 시각화 및 상세 분석

| <img src="./docs/산출물/나의페르소나_1.png" width="300" alt="페르소나_개방밍"> | <img src="./docs/산출물/나의페르소나_2.png" width="350" alt="페르소나_성실밍"> | <img src="./docs/산출물/나의페르소나_3.png" width="330" alt="페르소나_외향밍"> |
|:--:|:--:|:--:|
| 개방밍(O) | 성실밍(C) | 외향밍(E) |

| <img src="./docs/산출물/나의페르소나_4.png" width="400" alt="페르소나_친화밍"> | <img src="./docs/산출물/나의페르소나_5.png" width="345" alt="페르소나_신경밍"> |  |
|:--:|:--:|:--:|
| 친화밍(A) | 신경밍(N) |  |


*페르소나 세포들의 시각화 및 성격 요인 분석*

| ![개방성 리포트](./docs/산출물/SELF리포트_개방성.png) | ![성실성 리포트](./docs/산출물/SELF리포트_성실성.png) | ![외향성 리포트](./docs/산출물/SELF리포트_외향성.png) |
|:--:|:--:|:--:|
| 개방성 | 성실성 | 외향성 |


*각 성격 요인별 상세 리포트 (개방성, 성실성, 외향성)*

#### 4. AI 채팅
페르소나별 AI와의 1:1 대화를 통한 자기 성찰

<div align="center">
  <a href="https://youtube.com/shorts/06hj1ZCNq8E" target="_blank">
    <img src="https://img.youtube.com/vi/06hj1ZCNq8E/maxresdefault.jpg" alt="AI 채팅 영상" width="60%">
  </a>
  <p><em>5가지 페르소나 중 선택하여 실시간 대화 - <a href="https://youtube.com/shorts/06hj1ZCNq8E" target="_blank">YouTube에서 보기</a></em></p>
</div>

#### 5. 일기 & AI 분석
일기 작성 및 AI 페르소나 분석

<div align="center">
  <a href="https://youtube.com/shorts/V1c-FNLH0ro" target="_blank">
    <img src="https://img.youtube.com/vi/V1c-FNLH0ro/maxresdefault.jpg" alt="일기 & AI 분석 영상" width="60%">
  </a>
  <p><em>일기 작성부터 AI 페르소나 분석까지 전체 과정 - <a href="https://youtube.com/shorts/V1c-FNLH0ro" target="_blank">YouTube에서 보기</a></em></p>
</div>

#### 6. 행동 기반 성격 게임
게임 플레이를 통한 암묵적 성격 측정

<!-- ![게임 메인](./docs/산출물/[게임]메인.png) -->

| <img src="./docs/산출물/게임_메인_1.png" width="530" alt="게임 메인(1)"> | <img src="./docs/산출물/게임_메인_2.png" width="540" alt="게임 메인(2)"> | <img src="./docs/산출물/게임_메인_3.png" width="420" alt="게임 메인(3)"> |
|:--:|:--:|:--:|
| 버블게임 | 상어를 피하자 | 보물 나누기 |

*3가지 게임을 통한 행동 패턴 분석*

<details>
<summary><b>게임 상세 화면 보기</b></summary>

</br>

**풍선 게임 (위험 감수 성향 측정)**

| <img src="./docs/산출물/게임_버블_설명_1.png" width="400" alt="풍선게임 설명"> | <img src="./docs/산출물/게임_버블_게임진행.png" width="400" alt="풍선게임 진행"> | <img src="./docs/산출물/게임_버블_결과.png" width="360" alt="풍선게임 결과"> |
|:--:|:--:|:--:|
| 풍선게임 설명 | 풍선게임 진행 | 풍선게임 결과 |


<details>
<summary><b>📹 풍선 게임 플레이 영상</b></summary>
<div align="center">
  <a href="https://youtube.com/shorts/VuaeLCusSVg" target="_blank">
    <img src="https://img.youtube.com/vi/VuaeLCusSVg/maxresdefault.jpg" alt="풍선 게임 플레이 영상" width="60%">
  </a>
  <p><em>풍선을 터뜨리지 않고 얼마나 위험을 감수하는지 측정 - <a href="https://youtube.com/shorts/VuaeLCusSVg" target="_blank">YouTube에서 보기</a></em></p>
</div>
</details>

---

</br>

**최후통첩 게임 (공정성 및 협력 성향 측정)**

| <img src="./docs/산출물/게임_보물_설명_1.png" width="480" alt="보물게임 설명"> | <img src="./docs/산출물/게임_보물_진행_제안자_거절가능_1.png" width="400" alt="보물게임 진행"> | <img src="./docs/산출물/게임_보물_결과.png" width="500" alt="보물게임 결과"> |
|:--:|:--:|:--:|
| 보물게임 설명 | 보물게임 진행 | 보물게임 결과 |


<details>
<summary><b>📹 최후통첩 게임 플레이 영상</b></summary>
<div align="center">
  <a href="https://youtube.com/shorts/JbiaNtNyFJo" target="_blank">
    <img src="https://img.youtube.com/vi/JbiaNtNyFJo/maxresdefault.jpg" alt="최후통첩 게임 플레이 영상" width="60%">
  </a>
  <p><em>보물 분배 제안과 수락/거절을 통한 공정성 측정 - <a href="https://youtube.com/shorts/JbiaNtNyFJo" target="_blank">YouTube에서 보기</a></em></p>
</div>
</details>

---

</br>

**상어 피하기 게임 (스트레스 대처 및 협력 성향 측정)**

| <img src="./docs/산출물/게임_상어_설명_1.png" width="400" alt="상어게임 설명"> | <img src="./docs/산출물/게임_상어_게임진행.png" width="400" alt="상어게임 진행"> | <img src="./docs/산출물/게임_상어_결과.png" width="370" alt="상어게임 결과"> |
|:--:|:--:|:--:|
| 상어게임 설명 | 상어게임 진행 | 상어게임 결과 |

<details>
<summary><b>📹 상어 피하기 게임 플레이 영상</b></summary>
<div align="center">
  <a href="https://youtube.com/shorts/8PAqmjMRMlc" target="_blank">
    <img src="https://img.youtube.com/vi/8PAqmjMRMlc/maxresdefault.jpg" alt="상어 피하기 게임 플레이 영상" width="60%">
  </a>
  <p><em>스트레스 상황에서의 의사결정 패턴 분석 - <a href="https://youtube.com/shorts/8PAqmjMRMlc" target="_blank">YouTube에서 보기</a></em></p>
</div>
</details>

</br>

</details>

#### 7. 최종 리포트
자기보고식 검사와 행동 기반 분석의 통합 리포트

![최종 리포트](./docs/산출물/FINAL리포트_리포트메인.png)

*'내가 생각하는 나' vs '실제 나'의 간극 시각화*

---

### 시스템 설계 문서

#### User Flow
사용자의 전체 경험 흐름을 시각화한 다이어그램입니다.

![User Flow](./docs/기획/[너의OCEAN은]%20UserFlow.png)

#### 와이어프레임
주요 화면 구성과 UI/UX 설계입니다.

![Wireframe](./docs/기획/[너의OCEAN은]%20와이어프레임.png)

#### API 설계
백엔드 API 엔드포인트 구조입니다.

![API Design](./docs/기획/[너의OCEAN은]%20API.png)

#### ERD (Entity Relationship Diagram)
데이터베이스 구조 및 테이블 관계도입니다.

![ERD](./docs/기획/[너의OCEAN은]ERD.png)

---

## 주요 기능

### 1. 심층 성격 분석

#### Big Five 기반 성격 검사

**OCEAN 5대 성격 요인 측정**
- **O** (Openness): 개방성 - 새로운 경험과 아이디어에 대한 개방성
- **C** (Conscientiousness): 성실성 - 목표 지향성과 자기 통제력
- **E** (Extraversion): 외향성 - 사회적 상호작용과 활동성
- **A** (Agreeableness): 친화성 - 타인에 대한 배려와 협력성
- **N** (Neuroticism): 신경성 - 정서적 불안정성과 스트레스 민감성

#### 검사 방법
1. **자기보고식 설문**: 120문항의 표준화된 Big Five 검사
2. **행동 기반 게임**: 실제 선택과 반응을 통한 암묵적 성격 측정
3. **통합 분석**: 두 결과를 결합하여 더 정확한 프로필 생성

#### 성격 간극 시각화
- 레이더 차트를 통한 5요인 비교
- 각 요인별 상세 분석 및 해석
- 페르소나 세포 형태의 캐릭터 시각화
- 간극이 큰 요인에 대한 심층 분석 제공

### 2. AI 기반 상호작용

#### 일기 작성
**데일리 일기**
- 자유 형식의 일기 작성
- 하루의 생각과 경험 기록
- 달력 형태로 일기 관리

**AI 분석**
- 일기 내용을 5가지 페르소나 관점에서 분석
- 각 페르소나의 피드백 및 조언 제공
- 성격 특성과 연결된 인사이트 도출

#### AI 페르소나 대화
**1:1 맞춤형 대화**
- 5가지 성격 요인별 독립적인 AI 페르소나
- 각 페르소나는 고유한 성격과 관점을 보유
- 사용자의 고민, 상황에 대한 맞춤형 응답
- 대화 히스토리 기반 문맥 이해

**페르소나 특성**
- **개방성 페르소나**: 창의적이고 호기심 많은 조언자
- **성실성 페르소나**: 체계적이고 목표 지향적인 코치
- **외향성 페르소나**: 활기차고 사교적인 친구
- **친화성 페르소나**: 공감적이고 따뜻한 상담자
- **신경성 페르소나**: 신중하고 불안을 관리하는 수호자

#### AI 에이전트 회의
**내면의 토론 시뮬레이션**
- 사용자의 고민/상황에 대해 5개 페르소나가 회의
- 각 관점에서의 분석 및 조언 제시
- 페르소나 간 의견 교환 및 종합
- 최종 결론 및 실행 가능한 제안 도출

**회의 결과**
- 전체 대화 로그 제공
- 주요 인사이트 요약
- 실행 계획 및 체크리스트
- 추후 참고를 위한 북마크 기능

#### 외부 데이터 연동
**지원 플랫폼**
- 향후 Google Calendar, Todoist 등 확장 예정

**연동 효과**
- 생산성 패턴과 성격 특성 연결
- 더 풍부한 맥락 정보 기반 인사이트

### 3. 시각화 및 대시보드

#### 성격 분석 대시보드
- OCEAN 5요인 레이더 차트
- 자기보고 vs 행동분석 비교 차트
- 성격 유형 해석 및 설명
- 강점 및 개선 영역 제시

#### AI 대화 로그
- 전체 대화 기록 보관
- 키워드 검색 및 필터링
- 중요한 대화 북마크
- 인사이트 아카이브

---

## 기술 스택

### Frontend

![Next.js](https://img.shields.io/badge/Next.js_15-000000?style=for-the-badge&logo=next.js&logoColor=white)
![React](https://img.shields.io/badge/React_19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)

**주요 라이브러리**
- **TanStack Query v5**: 서버 상태 관리 및 캐싱
- **Zustand**: 클라이언트 상태 관리
- **Framer Motion**: 애니메이션 및 인터랙션
- **Chart.js**: 데이터 시각화
- **Radix UI**: 접근성을 고려한 UI 컴포넌트
- **Kysely**: 타입 안전 SQL 쿼리 빌더

### Backend

![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)

**주요 의존성**
- **Spring Data JPA**: ORM 및 데이터베이스 추상화
- **Spring Security + OAuth2**: 인증 및 소셜 로그인
- **Spring Kafka**: 이벤트 스트리밍
- **SpringDoc OpenAPI (Swagger)**: API 문서 자동 생성
- **MinIO SDK**: 객체 스토리지 클라이언트
- **Lombok**: 보일러플레이트 코드 감소

### AI/ML

![Python](https://img.shields.io/badge/Python_3.9+-3776AB?style=for-the-badge&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![LangChain](https://img.shields.io/badge/LangChain-121212?style=for-the-badge&logo=chainlink&logoColor=white)
![Claude](https://img.shields.io/badge/Claude_AI-9B4DCA?style=for-the-badge&logo=anthropic&logoColor=white)

**주요 라이브러리**
- **LangChain & LangGraph**: AI 에이전트 오케스트레이션
- **Anthropic Claude API**: 대화형 AI 페르소나
- **Streamlit**: AI 모델 데모 및 디버깅

### Database & Storage

![PostgreSQL](https://img.shields.io/badge/PostgreSQL_16-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![MinIO](https://img.shields.io/badge/MinIO-C72E49?style=for-the-badge&logo=minio&logoColor=white)

**데이터베이스 역할**
- **PostgreSQL**: 주요 관계형 데이터 (사용자, 검사 결과, 일기, AI 대화 로그)
- **Redis**: Refresh Token 저장, 온보딩 페이지 한마디 API 1시간 캐시, 게임 횟수 캐시
- **MinIO**: 사용자 업로드 파일 및 이미지 저장

### Data Streaming

![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Kafka Connect](https://img.shields.io/badge/Kafka_Connect-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)

**스트리밍 아키텍처**
- **Kafka**: 실시간 이벤트 스트리밍 (일기 작성, AI 대화 이벤트, 게임 결과 등)
- **Kafka Sink Connector**: MinIO 자동 전송 (로그 백업)

### DevOps & Infrastructure

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![AWS](https://img.shields.io/badge/AWS_EC2-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)

**인프라 구성**
- **Docker Compose**: 컨테이너 오케스트레이션
- **Jenkins**: CI/CD 파이프라인
- **Nginx**: 리버스 프록시 및 로드 밸런싱
- **Let's Encrypt**: SSL/TLS 인증서
- **Grafana + Prometheus**: 모니터링 및 메트릭
- **AWS EC2 (2 Instances)**: 서버 호스팅

---

## 시스템 아키텍처

### 전체 아키텍처

```mermaid
graph TB
    %% 사용자 레이어
    User[Users Web Browser]

    %% 프록시 레이어
    Nginx[Nginx Reverse Proxy<br/>- SSL Termination<br/>- Static File Serving]

    %% 애플리케이션 레이어
    Frontend[Frontend Next.js<br/>- SSR/CSR<br/>- React 19<br/>- TanStack Query]
    Backend[Backend Spring Boot<br/>- REST API<br/>- WebSocket<br/>- OAuth2]

    %% 데이터베이스 레이어
    PostgreSQL[(PostgreSQL<br/>- User Data<br/>- Personality<br/>- Diary)]
    Redis[(Redis<br/>- Refresh Token<br/>- Onboarding Cache 1h<br/>- Game Count Cache)]
    MinIO[(MinIO<br/>Object Storage)]

    %% 메시징 레이어
    Kafka[Apache Kafka<br/>- Events Stream<br/>- Message Queue]

    %% AI 레이어
    AIServer[AI Server FastAPI<br/>- LangChain/LangGraph<br/>- Persona Agents<br/>- Claude API]

    %% 연결 관계
    User -->|HTTPS:443| Nginx
    Nginx --> Frontend
    Nginx --> Backend
    Frontend <-->|REST API| Backend

    Backend --> PostgreSQL
    Backend --> Redis
    Backend --> MinIO
    Backend --> Kafka

    Kafka --> AIServer

    %% 스타일링
    classDef userClass fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    classDef proxyClass fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef appClass fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef dbClass fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px
    classDef msgClass fill:#fff8e1,stroke:#f57f17,stroke-width:2px
    classDef aiClass fill:#fce4ec,stroke:#880e4f,stroke-width:2px

    class User userClass
    class Nginx proxyClass
    class Frontend,Backend appClass
    class PostgreSQL,Redis,MinIO dbClass
    class Kafka msgClass
    class AIServer aiClass
```

### 서버 구성

**Server A (Main Application Server)**
- Frontend (Next.js) - Port 3000
- Backend (Spring Boot) - Port 8080
- AI Server (FastAPI) - Port 8000
- PostgreSQL Database
- Redis Cache
- Nginx Reverse Proxy
- Jenkins CI/CD
- Grafana Monitoring

**Server B (Data Infrastructure Server)**
- Apache Kafka (2 Brokers) + Zookeeper
- Kafka Sink Connector (MinIO 백업)
- MinIO Object Storage (4 Nodes 분산 모드)
- Apache Spark (Master + Worker)
- Kafka UI (Web Interface)
- Grafana Monitoring

### 데이터 플로우

#### 1. 사용자 인증 플로우

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Nginx
    participant Backend
    participant Redis
    participant OAuth2

    User->>Frontend: 로그인 요청
    Frontend->>Nginx: HTTPS 요청
    Nginx->>Backend: 인증 요청
    Backend->>OAuth2: OAuth2 인증
    OAuth2-->>Backend: 인증 토큰
    Backend->>Redis: Refresh Token 저장
    Backend-->>Frontend: Access Token 반환
    Frontend-->>User: 로그인 완료
```

#### 2. 성격 검사 플로우

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Backend
    participant PostgreSQL

    User->>Frontend: 성격 검사 완료
    Frontend->>Backend: 검사 결과 제출
    Backend-->>Backend: Big5 점수 계산
    Backend->>PostgreSQL: 결과 저장
    Backend-->>Frontend: 계산 완료
    Frontend-->>User: 결과 표시
```

#### 3. AI 대화 플로우

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Backend
    participant Kafka
    participant AI Server
    participant Claude API
    participant PostgreSQL

    User->>Frontend: 메시지 입력
    Frontend->>Backend: 채팅 요청
    Backend->>Kafka: 메시지 이벤트
    Kafka->>AI Server: 이벤트 전달
    AI Server->>Claude API: LangChain 처리
    Claude API-->>AI Server: AI 응답
    AI Server-->>Backend: 응답 반환
    Backend->>PostgreSQL: 대화 로그 저장
    Backend-->>Frontend: 응답 전달
    Frontend-->>User: 메시지 표시
```

#### 4. 일기 분석 플로우

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Backend
    participant PostgreSQL
    participant Kafka
    participant AI Server

    User->>Frontend: 일기 작성
    Frontend->>Backend: 일기 제출
    Backend->>PostgreSQL: 일기 저장
    Backend->>Kafka: 일기 작성 이벤트
    Kafka->>AI Server: 이벤트 전달
    AI Server-->>AI Server: 페르소나 분석
    AI Server-->>Backend: 분석 결과
    Backend->>PostgreSQL: 분석 결과 저장
    Backend-->>Frontend: 분석 완료
    Frontend-->>User: 페르소나 피드백 표시
```

---

## 프로젝트 구조

### 모노레포 구조

```
my-ocean/
├── frontend/              # Next.js 프론트엔드
│   ├── app/              # Next.js App Router
│   ├── components/       # 재사용 가능한 컴포넌트
│   ├── features/         # 기능별 모듈 (Feature-Sliced Design)
│   ├── hooks/            # Custom React Hooks
│   ├── lib/              # 유틸리티 및 헬퍼
│   ├── stores/           # Zustand 상태 관리
│   ├── types/            # TypeScript 타입 정의
│   └── tests/            # E2E 및 유닛 테스트
│
├── backend/              # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/myocean/
│   │   │   │   ├── config/          # 설정 클래스
│   │   │   │   ├── controller/      # REST API 컨트롤러
│   │   │   │   ├── service/         # 비즈니스 로직
│   │   │   │   ├── repository/      # JPA 레포지토리
│   │   │   │   ├── entity/          # JPA 엔티티
│   │   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   │   ├── security/        # 인증/인가
│   │   │   │   └── kafka/           # Kafka 프로듀서/컨슈머
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── build.gradle
│
├── ai/                   # AI 서버 (FastAPI + LangChain)
│   ├── ai_server.py                  # FastAPI 메인 서버
│   ├── enhanced_langgraph_personas.py # Multi-Agent 시스템
│   ├── reasoning_persona_system.py   # 추론 기반 페르소나
│   ├── gms_client.py                 # 게임 행동 분석 클라이언트
│   ├── requirements.txt
│   └── README_AGENT.md
│
├── infra/                # 인프라 구성
│   ├── server-a/         # 메인 서버 설정
│   │   ├── docker-compose.yml
│   │   ├── nginx/
│   │   └── jenkins/
│   └── server-b/         # 데이터 서버 설정
│       ├── docker-compose.yml
│       └── ocean-data/   # Kafka, MinIO
│
└── docs/                 # 프로젝트 문서
    ├── 기획안.md
    ├── 요구사항 명세서.md
    └── 특화프로젝트_너의OCEAN은/
        ├── 기획/
        ├── 산출물/
        └── 인프라/
```

---


## 개발 가이드

### 코드 스타일

#### Frontend
- **ESLint + Prettier**: 자동 포맷팅 및 린팅
- **Husky**: Pre-commit 훅으로 코드 품질 보장
- **Commitlint**: Conventional Commits 규칙 준수

```bash
# 린트 실행
npm run lint

# 자동 수정
npm run lint:fix

# 타입 체크
npm run typecheck
```

#### Backend
- **Google Java Style Guide** 준수
- **Lombok**: 보일러플레이트 감소
- **JavaDoc**: Public API 문서화

### Git 워크플로우

#### 브랜치 전략
```
main            # 프로덕션 브랜치
├── develop     # 개발 통합 브랜치
    ├── feature/기능명   # 새 기능 개발
    ├── fix/버그명       # 버그 수정
    └── refactor/내용    # 리팩토링
```

#### 커밋 메시지 규칙 (Conventional Commits)
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드
chore: 빌드/설정 변경
```

**예시**
```bash
feat: Add AI persona chat feature
fix: Resolve personality chart rendering issue
docs: Update API documentation for personality test
```

### API 문서

#### Backend API
- Swagger UI 자동 생성: `/swagger-ui/index.html`
- OpenAPI 3.0 Spec: `/v3/api-docs`

#### AI Server API
- FastAPI 자동 문서: `/docs` (Swagger UI)
- ReDoc: `/redoc`

---

## 팀원 소개

| 이름 | 역할 | 담당 업무 |
|------|------|----------|
| [김진효](https://github.com/Pong0882) | **Frontend Developer** | 와이어프레임 설계, 설문/게임/친구/리포트 차트 기능 구현  |
| [류지선](https://github.com/jisun24) | **Backend Developer** | 게임 기능 (풍선게임, 최후통첩게임) |
| [박재호](https://github.com/ppnyoong9) | **DevOps Engineer** | 인프라 구축, OAuth2.0 개발 |
| [정봉기](https://github.com/JB0527) | **AI/ML Engineer** | LangChain AI 에이전트 시스템 구축 |
| [최경민](https://github.com/kyngmn) | **Frontend Developer** | 아키텍처 설계, 일기/채팅 기능 구현 |
| [황지현](https://github.com/sjihyun0756) | **Backend Lead** | 백엔드 기능 전반 구현, AI 응답 파싱 개선 |

---

## 관련 문서

### 기획 문서
- [프로젝트 기획안](docs/기획안.md)
- [요구사항 명세서](docs/요구사항%20명세서.md)
- [기능 정의서](docs/기획/[너의OCEAN은]기능정의서.pdf)

### 기술 문서
- [API 명세서](https://be.myocean.cloud/swagger-ui/index.html)
- [포팅 매뉴얼](docs/인프라/포팅매뉴얼.md)
- [AI Agent 문서](ai/README_AGENT.md)
- [배포 가이드](ai/DEPLOYMENT.md)

### 발표 자료
- [중간 발표](https://drive.google.com/file/d/1TVhBkKR0zvbFDPvJH4iypBe9zPFmsJ0q/view?usp=drive_link)
- [최종 발표](https://drive.google.com/file/d/1yLevzGO3DfyCaTwaOQr802GSZ2Ih6qkX/view?usp=drive_link)
