#!/bin/bash
# 🚀 Kafka 토픽 바로 생성 스크립트

echo "📋 s3-test-logs 토픽 생성 중..."

# s3-test-logs 토픽 생성 (S3 Sink Connector용)
kafka-topics --bootstrap-server kafka1:9092 --create --if-not-exists \
    --topic s3-test-logs \
    --partitions 4 \
    --replication-factor 2

echo "✅ s3-test-logs 토픽 생성 완료"

echo "📊 생성된 토픽 목록:"
kafka-topics --bootstrap-server kafka1:9092 --list

echo "✅ Kafka 토픽 설정 완료!"