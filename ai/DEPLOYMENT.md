# 🚀 Big5 추론 페르소나 시스템 배포 가이드

EC2에서 Docker로 간단하게 배포할 수 있는 가이드입니다.

## 📋 사전 준비사항

### 1. EC2 인스턴스 요구사항
- **권장 사양**: t3.large (2 vCPU, 8GB RAM) 이상
- **최소 사양**: t3.medium (2 vCPU, 4GB RAM)
- **저장소**: 20GB 이상
- **OS**: Ubuntu 20.04 LTS 또는 Amazon Linux 2

### 2. 보안 그룹 설정
다음 포트를 열어주세요:
- **8501**: Streamlit 웹 인터페이스
- **22**: SSH (관리용)

## 🔧 설치 및 배포

### 1단계: 코드 다운로드
```bash
# Git clone 또는 파일 업로드
git clone [your-repository]
cd ai
```

### 2단계: API 키 설정
```bash
# .env 파일 생성 및 편집
echo 'GMS_KEY=your_actual_api_key_here' > .env
nano .env  # 실제 API 키로 수정
```

### 3단계: Docker 설치 (Ubuntu)
```bash
# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 로그아웃 후 다시 로그인 (그룹 권한 적용)
```

### 4단계: 원클릭 배포
```bash
# 배포 스크립트 실행
./deploy.sh
```

### 5단계: 접속 확인
- **로컬 접속**: http://localhost:8501
- **외부 접속**: http://[EC2_PUBLIC_IP]:8501

## 🛠️ 관리 명령어

### 서비스 관리
```bash
# 서비스 시작
docker-compose up -d

# 서비스 정지
docker-compose down

# 서비스 재시작
docker-compose restart

# 로그 확인
docker-compose logs -f

# 서비스 상태 확인
docker-compose ps
```

### 업데이트
```bash
# 코드 업데이트 후
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

## 📊 모니터링

### 헬스체크
```bash
# 서비스 상태 확인
curl http://localhost:8501/_stcore/health

# 컨테이너 상태 확인
docker ps
```

### 로그 모니터링
```bash
# 실시간 로그 보기
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f big5-reasoning-app
```

## 🔒 보안 설정

### 1. 방화벽 설정 (Ubuntu)
```bash
sudo ufw allow 22
sudo ufw allow 8501
sudo ufw enable
```

### 2. SSL/HTTPS 설정 (선택사항)
Nginx를 사용한 리버스 프록시 설정:

```nginx
# /etc/nginx/sites-available/big5-app
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8501;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 🚨 문제 해결

### 자주 발생하는 문제들

#### 1. 포트 8501이 이미 사용 중
```bash
# 포트 사용 중인 프로세스 확인
sudo lsof -i :8501
sudo kill -9 [PID]
```

#### 2. 메모리 부족
```bash
# 스왑 파일 생성 (2GB)
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

#### 3. Docker 권한 오류
```bash
# Docker 그룹에 사용자 추가
sudo usermod -aG docker $USER
# 로그아웃 후 다시 로그인
```

#### 4. API 키 오류
```bash
# .env 파일 확인
cat .env
# API 키가 올바른지 확인
```

## 📈 성능 최적화

### 리소스 제한 조정
`docker-compose.yml`에서 메모리 제한을 조정할 수 있습니다:

```yaml
deploy:
  resources:
    limits:
      memory: 8G  # 메모리 증가
    reservations:
      memory: 4G
```

### 모델 캐시 활용
첫 실행 시 모델 다운로드로 시간이 소요되지만, 이후에는 캐시를 사용합니다.

## 🔄 백업 및 복구

### 데이터 백업
```bash
# 볼륨 백업
docker run --rm -v ai_huggingface_cache:/source -v $(pwd):/backup ubuntu tar czf /backup/cache-backup.tar.gz -C /source .
```

### 복구
```bash
# 볼륨 복구
docker run --rm -v ai_huggingface_cache:/target -v $(pwd):/backup ubuntu tar xzf /backup/cache-backup.tar.gz -C /target
```

## 📞 지원

문제가 발생하면:
1. 로그 확인: `docker-compose logs -f`
2. 컨테이너 상태 확인: `docker ps`
3. 헬스체크 확인: `curl http://localhost:8501/_stcore/health`

---

🎉 **축하합니다!** Big5 추론 페르소나 시스템이 성공적으로 배포되었습니다!