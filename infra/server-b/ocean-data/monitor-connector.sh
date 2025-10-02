#!/bin/bash
# 🔍 S3 Sink Connector 실시간 모니터링

echo "🔍 S3 Sink Connector 모니터링 시작..."

while true; do
    clear
    echo "⏰ $(date)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    # Connector 상태
    echo "📡 S3 Sink Connector 상태:"
    STATUS=$(curl -s http://localhost:8084/connectors/s3-sink-test/status)

    if echo "$STATUS" | grep -q "RUNNING"; then
        echo "  ✅ Connector: RUNNING"
        RUNNING_TASKS=$(echo "$STATUS" | grep -o '"state":"RUNNING"' | wc -l)
        echo "  ✅ Running Tasks: $RUNNING_TASKS/4"
    else
        echo "  ❌ Connector: FAILED"
    fi

    echo ""
    echo "📊 저장된 파일 수:"
    FILE_COUNT=$(find data/minio1/logs-server1 -name "*.json" 2>/dev/null | wc -l)
    echo "  📁 MinIO Files: $FILE_COUNT"

    echo ""
    echo "🔄 Kafka Consumer Group 상태:"
    docker exec kafka1 kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group connect-s3-sink-test 2>/dev/null | tail -n +3

    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "⏱️  2초 후 갱신... (Ctrl+C로 종료)"
    sleep 2
done