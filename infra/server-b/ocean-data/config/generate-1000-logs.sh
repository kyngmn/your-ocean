#!/bin/bash
# 🚀 1000개 실제 로그 데이터 생성 및 처리 테스트

echo "🎯 1000개 로그 데이터 실시간 처리 테스트 시작..."
echo "📊 시작 시간: $(date)"

# 현재 오프셋 기록
echo "📈 처리 전 상태:"
docker exec kafka1 kafka-run-class kafka.tools.GetOffsetShell --bootstrap-server kafka1:29092 --topic s3-test-logs

echo ""
echo "📤 1000개 로그 데이터 전송 시작..."

# 실제 사용자 행동 패턴을 시뮬레이션한 로그 생성
for i in {1..1000}; do
  # 다양한 로그 타입 생성
  case $((i % 5)) in
    0) LOG_TYPE="user_action"
       ACTION="click"
       PAGE="/product/$(($RANDOM % 100))"
       ;;
    1) LOG_TYPE="error"
       ACTION="404_error"
       PAGE="/missing/page$(($RANDOM % 50))"
       ;;
    2) LOG_TYPE="login"
       ACTION="user_login"
       PAGE="/auth/login"
       ;;
    3) LOG_TYPE="purchase"
       ACTION="buy_item"
       PAGE="/checkout/$(($RANDOM % 20))"
       ;;
    4) LOG_TYPE="search"
       ACTION="search_query"
       PAGE="/search?q=item$(($RANDOM % 200))"
       ;;
  esac

  # JSON 로그 생성
  LOG_DATA=$(cat <<EOF
{
  "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%S.%3NZ)",
  "log_id": "$i",
  "user_id": "user$(($RANDOM % 500))",
  "session_id": "sess$(($RANDOM % 100))",
  "log_type": "$LOG_TYPE",
  "action": "$ACTION",
  "page": "$PAGE",
  "ip_address": "192.168.1.$(($RANDOM % 255))",
  "user_agent": "Mozilla/5.0 (Test Browser)",
  "response_time": $(($RANDOM % 3000))ms,
  "status_code": $((200 + $RANDOM % 300)),
  "server": "distributed-cluster-$((i % 4))"
}
EOF
)

  # 카프카로 전송
  echo "$LOG_DATA" | docker exec -i kafka1 kafka-console-producer \
    --bootstrap-server kafka1:29092,kafka2:29092 \
    --topic s3-test-logs >/dev/null 2>&1

  # 진행 상황 표시 (100개마다)
  if [ $((i % 100)) -eq 0 ]; then
    echo "  ✅ $i/1000 로그 전송 완료 ($(date +%H:%M:%S))"
  fi

  # 처리 부하 조절 (너무 빠르면 시스템 과부하)
  if [ $((i % 50)) -eq 0 ]; then
    sleep 1
  fi
done

echo ""
echo "📊 전송 완료 시간: $(date)"
echo "⏰ Sink Connector 처리 대기 중... (30초)"
sleep 30

echo ""
echo "📈 처리 후 상태:"
docker exec kafka1 kafka-run-class kafka.tools.GetOffsetShell --bootstrap-server kafka1:29092 --topic s3-test-logs

echo ""
echo "🔍 Consumer Group 처리 상태:"
docker exec kafka1 kafka-consumer-groups --bootstrap-server kafka1:29092 --describe --group connect-s3-sink-test

echo ""
echo "💾 MinIO 저장 결과 확인:"
echo "📁 전체 파일 수:"
docker exec minio1 mc ls --recursive local/logs-server1/topics/s3-test-logs/ | wc -l

echo ""
echo "📊 파티션별 저장 현황:"
for partition in 0 1 2 3; do
  FILE_COUNT=$(docker exec minio1 mc ls local/logs-server1/topics/s3-test-logs/partition=$partition/ | wc -l)
  echo "  Partition $partition: $FILE_COUNT 파일"
done

echo ""
echo "🎉 1000개 로그 처리 테스트 완료!"
echo "📈 처리 결과: 카프카 → Sink Connector → MinIO 분산 클러스터"