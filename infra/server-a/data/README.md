# MinIO 분산 클러스터 배포 가이드

## 🏗️ 아키텍처 개요

```
┌─────────────────────────────────────────────────────────────────┐
│                    분산 MinIO 클러스터                           │
└─────────────────────────────────────────────────────────────────┘

Server 1 (data2.myocean.cloud:443)          Server 2 (data.myocean.cloud)
├── nginx SSL → /minio1/ → minio1:9001       ├── minio3:9000 (이미 실행중)
├── nginx SSL → /minio2/ → minio2:9001       └── minio4:9002 (이미 실행중)
├── nginx SSL → /minio1-api/ → minio1:9000
└── nginx SSL → /minio2-api/ → minio2:9000
```

## 📋 배포 단계

### 1. Server 1에서 MinIO 배포

```bash
# 1. 작업 디렉토리로 이동
cd /path/to/S13P21A303/infra

# 2. ocean-net 네트워크 생성 (없는 경우)
docker network create ocean-net

# 3. MinIO 서비스 시작
cd data
docker-compose -f docker-compose.minio.yml up -d

# 4. 컨테이너 상태 확인
docker ps | grep minio
```

### 2. nginx 설정 리로드

```bash
# nginx 컨테이너 재시작 (새 설정 적용)
docker-compose restart nginx

# 또는 설정 리로드
docker exec web-nginx nginx -s reload
```

### 3. Server 2에서 기존 MinIO 재구성

Server 2에서 기존 MinIO를 분산 클러스터 모드로 재시작:

```bash
# 기존 MinIO 중지
docker stop minio1 minio2 minio3 minio4

# 분산 모드로 재시작
docker run -d --name minio3 \
  --network ocean-net-data \
  -p 9000:9000 -p 9001:9001 \
  -v /home/ubuntu/ocean-data/data/minio3:/data \
  -e MINIO_ROOT_USER=hellopong \
  -e MINIO_ROOT_PASSWORD=hellopong303 \
  minio/minio server \
    https://data2.myocean.cloud/minio1-api/data \
    https://data2.myocean.cloud/minio2-api/data \
    http://data.myocean.cloud:9000/data \
    http://data.myocean.cloud:9002/data \
    --console-address :9001

docker run -d --name minio4 \
  --network ocean-net-data \
  -p 9002:9000 -p 9003:9001 \
  -v /home/ubuntu/ocean-data/data/minio4:/data \
  -e MINIO_ROOT_USER=hellopong \
  -e MINIO_ROOT_PASSWORD=hellopong303 \
  minio/minio server \
    https://data2.myocean.cloud/minio1-api/data \
    https://data2.myocean.cloud/minio2-api/data \
    http://data.myocean.cloud:9000/data \
    http://data.myocean.cloud:9002/data \
    --console-address :9001
```

## 🔗 접근 URL

### Server 1 (data2.myocean.cloud)
- **MinIO1 Console**: https://data2.myocean.cloud/minio1/
- **MinIO2 Console**: https://data2.myocean.cloud/minio2/
- **MinIO1 API**: https://data2.myocean.cloud/minio1-api/
- **MinIO2 API**: https://data2.myocean.cloud/minio2-api/

### Server 2 (data.myocean.cloud)
- **MinIO3 Console**: http://data.myocean.cloud:8181/
- **MinIO4 Console**: http://data.myocean.cloud:8183/
- **MinIO3 API**: http://data.myocean.cloud:9000/
- **MinIO4 API**: http://data.myocean.cloud:9002/

## 🔑 인증 정보
- **Username**: `hellopong`
- **Password**: `hellopong303`

## 🧪 테스트

### 1. 클러스터 상태 확인
```bash
# Server 1에서
curl -I https://data2.myocean.cloud/minio1-api/minio/health/live

# Server 2에서
curl -I http://data.myocean.cloud:9000/minio/health/live
```

### 2. 웹 콘솔 접근
- https://data2.myocean.cloud/minio1/
- http://data.myocean.cloud:8181/

### 3. S3 API 테스트
```bash
# 버킷 목록 조회 (인증 필요)
curl -X GET \
  -H "Authorization: AWS4-HMAC-SHA256 ..." \
  https://data2.myocean.cloud/minio1-api/
```

## ⚠️ 주의사항

1. **동시 시작**: 모든 MinIO 노드가 거의 동시에 시작되어야 함
2. **방화벽**: Server 간 9000, 9002 포트 통신 허용 필요
3. **SSL 인증서**: Server 1의 SSL 인증서가 data2.myocean.cloud에 유효해야 함
4. **데이터 백업**: 기존 Server 2 데이터 백업 권장

## 🔧 트러블슈팅

### MinIO 노드 연결 실패
```bash
# 로그 확인
docker logs server1-minio1
docker logs server1-minio2

# 네트워크 연결 테스트
docker exec server1-minio1 ping data.myocean.cloud
```

### nginx 프록시 오류
```bash
# nginx 로그 확인
docker logs web-nginx

# 설정 문법 검사
docker exec web-nginx nginx -t
```


docker-compose -f docker-compose.minio.yml up -d