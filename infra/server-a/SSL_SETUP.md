# SSL 인증서 설정 가이드 (data2.myocean.cloud 추가)

## 🔐 현재 SSL 설정

### 도메인 목록
- `myocean.cloud` (기존)
- `be.myocean.cloud` (기존)
- `data2.myocean.cloud` (신규 추가)

## 🚀 SSL 인증서 갱신 방법

### 1. Server 1에서 SSL 인증서 갱신

```bash
# 1. 작업 디렉토리로 이동
cd /path/to/S13P21A303/infra/nginx

# 2. certbot으로 인증서 갱신 실행
docker-compose up certbot

# 3. 로그 확인
docker logs certbot

# 4. nginx 재시작하여 새 인증서 적용
cd ../
docker-compose restart nginx
```

### 2. 인증서 갱신 확인

```bash
# SSL 인증서 유효성 확인
openssl x509 -in nginx/certs/live/myocean.cloud/fullchain.pem -text -noout | grep -A 1 "Subject Alternative Name"

# 웹에서 확인
curl -I https://data2.myocean.cloud/
```

### 3. 자동 갱신 설정 (선택사항)

```bash
# crontab에 추가 (월 1회 자동 갱신)
0 0 1 * * cd /path/to/S13P21A303/infra/nginx && docker-compose up certbot && cd ../ && docker-compose restart nginx
```

## 🧪 테스트 방법

### 1. DNS 확인
```bash
nslookup data2.myocean.cloud
# 결과: 43.203.252.173 (Server 1 IP)
```

### 2. SSL 연결 테스트
```bash
# HTTPS 연결 테스트
curl -I https://data2.myocean.cloud/

# SSL 인증서 정보 확인
openssl s_client -connect data2.myocean.cloud:443 -servername data2.myocean.cloud
```

### 3. MinIO 접근 테스트
```bash
# MinIO 콘솔 접근
curl -I https://data2.myocean.cloud/minio1/

# MinIO API 접근
curl -I https://data2.myocean.cloud/minio1-api/minio/health/live
```

## ⚠️ 주의사항

1. **DNS 전파**: DNS 변경 후 전파까지 최대 24시간 소요
2. **방화벽**: Server 1의 80/443 포트가 열려있어야 함
3. **webroot**: nginx의 `/var/www/certbot` 경로 접근 가능해야 함
4. **권한**: certbot이 `/etc/letsencrypt` 디렉토리에 쓰기 권한 필요

## 🔧 트러블슈팅

### certbot 실패 시
```bash
# 상세 로그 확인
docker logs certbot

# nginx 설정 확인
docker exec web-nginx nginx -t

# 웹루트 권한 확인
ls -la nginx/html/
```

### SSL 인증서 적용 실패 시
```bash
# nginx 재시작
docker-compose restart nginx

# 인증서 파일 확인
ls -la nginx/certs/live/myocean.cloud/

# nginx 에러 로그 확인
docker logs web-nginx
```

## 📁 파일 구조
```
S13P21A303/infra/
├── nginx/
│   ├── docker-compose.yml     (certbot 설정)
│   ├── conf.d/
│   │   ├── myocean_ssl.conf   (SSL 인증서 경로)
│   │   └── minio_proxy.conf   (data2 MinIO 프록시)
│   ├── certs/                 (SSL 인증서 저장소)
│   └── html/                  (webroot)
└── data/
    └── docker-compose.minio.yml (MinIO 서비스)
```