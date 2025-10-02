# Node.js 22 기반 이미지 사용
FROM node:22-alpine

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package*.json ./

# 의존성 설치
RUN npm ci --ignore-scripts

# 애플리케이션 코드 복사
COPY . .

# 빌드 시점에 환경변수 설정
ENV NEXT_PUBLIC_API_URL=https://be.myocean.cloud

# Next.js 빌드
RUN npm run build

# 포트 노출
EXPOSE 3000

# 환경 변수 설정 (운영환경)
ENV NODE_ENV=production

# Next.js 서버 시작
CMD ["npm", "start"]
