#!/bin/bash

# Big5 추론 페르소나 시스템 배포 스크립트
set -e

echo "🚀 Big5 추론 페르소나 시스템 배포를 시작합니다..."

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 함수: 성공 메시지
success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# 함수: 정보 메시지
info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# 함수: 에러 메시지
error() {
    echo -e "${RED}❌ $1${NC}"
}

# 1. .env 파일 확인
info "API 키 설정 확인 중..."
if [ ! -f .env ]; then
    error ".env 파일이 없습니다!"
    echo "다음 명령으로 .env 파일을 생성하세요:"
    echo "echo 'GMS_KEY=your_actual_api_key' > .env"
    exit 1
fi

if grep -q "your_api_key_here" .env; then
    error ".env 파일에 실제 API 키를 설정해주세요!"
    echo "편집: nano .env"
    exit 1
fi

success ".env 파일 확인 완료"

# 2. Docker 설치 확인
info "Docker 설치 확인 중..."
if ! command -v docker &> /dev/null; then
    error "Docker가 설치되지 않았습니다!"
    echo "Docker 설치 후 다시 실행하세요."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    error "docker-compose가 설치되지 않았습니다!"
    echo "docker-compose 설치 후 다시 실행하세요."
    exit 1
fi

success "Docker 환경 확인 완료"

# 3. 기존 컨테이너 정지 및 제거
info "기존 컨테이너 정리 중..."
docker-compose down 2>/dev/null || true
success "기존 컨테이너 정리 완료"

# 4. Docker 이미지 빌드
info "Docker 이미지 빌드 중... (시간이 소요될 수 있습니다)"
docker-compose build --no-cache
success "Docker 이미지 빌드 완료"

# 5. 컨테이너 실행
info "컨테이너 실행 중..."
docker-compose up -d
success "컨테이너 실행 완료"

# 6. 헬스체크 대기
info "서비스 시작 대기 중..."
sleep 10

# 7. 서비스 상태 확인
info "서비스 상태 확인 중..."
if curl -f http://localhost:8501/_stcore/health &> /dev/null; then
    success "서비스가 정상적으로 실행 중입니다!"
    echo ""
    echo "🌐 웹 브라우저에서 다음 주소로 접속하세요:"
    echo "   http://localhost:8501"
    echo ""
    echo "🖥️  EC2에서 실행 중이라면:"
    echo "   http://[EC2_PUBLIC_IP]:8501"
    echo ""
    echo "📋 로그 확인: docker-compose logs -f"
    echo "🛑 정지: docker-compose down"
else
    error "서비스 시작에 실패했습니다!"
    echo ""
    echo "로그를 확인하세요:"
    echo "docker-compose logs"
    exit 1
fi

success "배포 완료! 🎉"