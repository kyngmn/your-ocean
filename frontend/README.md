# Commit Message Guide

## 기본 규칙 (Conventional Commits)

```
<type>(<scope>): <subject>
```

- **type**: 변경 성격
- **scope**: 변경된 영역 (선택)
- **subject**: 간결한 설명 (마침표 X, 현재 시제)

---

## 1. Type 목록

- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 수정 (README, 주석 등)
- **style**: 코드 포맷팅, 세미콜론 등 (코드 변경 없음)
- **refactor**: 코드 리팩터링 (동작 변화 없음)
- **test**: 테스트 코드 추가/수정
- **chore**: 빌드, 설정, 패키지 변경 등
- **perf**: 성능 최적화
- **ci**: CI/CD 관련 설정 변경

---

## 2. Scope 예시 (Next.js + TS 기준)

- **app**: `src/app` 관련
- **api**: API 핸들러 (`src/app/handler/...`)
- **ui**: UI 컴포넌트 (`src/ui/...`)
- **store**: 전역 상태 관리 (zustand, recoil 등)
- **auth**: 인증/인가 로직
- **config**: eslint, prettier, tsconfig, next.config 등
- **deps**: 의존성 관련

---

## 3. Subject 작성 규칙

- 현재 시제, 간결하게 (영어/한국어 혼용 가능)
- 마침표 붙이지 않음
- 50자 이내 권장

✅ 예시

- `feat(auth): add JWT login flow`
- `fix(ui): button alignment issue in header`
- `docs(readme): add setup guide`
- `chore(config): update eslint rules`
- `refactor(api): simplify user fetch logic`

❌ 잘못된 예시

- `fixed bug` (시제 오류)
- `Update login function.` (마침표 불필요)
- `feat: add` (설명이 불충분)

---

## 4. Body (선택)

- `왜` 변경했는지, 구체적 설명
- 여러 줄 가능
- e.g.

```
fix(api): handle null sessionId

- Added null check for sessionId
- Prevents server crash when cookie is missing
```

---

## 5. Footer (선택)

- Breaking change, 관련 이슈 번호 표시
- e.g.

```
BREAKING CHANGE: user API response no longer returns password field
Closes #123
```
