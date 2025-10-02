#!/bin/bash
# 🔄 커넥터 자동 생성 스크립트

echo "⏰ Kafka Connect 서비스 대기 중..."
sleep 30

echo "🔗 S3 Sink Connector 자동 생성..."

# S3 Sink Connector 생성
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @/etc/kafka-connect/connectors/s3-sink-test.json

echo "✅ 커넥터 자동 생성 완료!"