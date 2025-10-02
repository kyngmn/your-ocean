#!/usr/bin/env python3
from kafka import KafkaProducer
import json
import time
from datetime import datetime

print("🚀 Kafka 연결 테스트 시작...")

# Kafka Producer 생성
try:
    producer = KafkaProducer(
        bootstrap_servers=['data.myocean.cloud:9094'],
        value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        request_timeout_ms=10000,  # 10초 타임아웃
        retries=3
    )
    print("✅ Kafka Producer 생성 성공!")
except Exception as e:
    print(f"❌ Producer 생성 실패: {e}")
    exit(1)

# 테스트 메시지 5개 전송
for i in range(5):
    test_log = {
        "timestamp": datetime.now().isoformat(),
        "level": "INFO",
        "service": "local-test",
        "message": f"Hello from local computer! Message #{i+1}",
        "user_id": f"test-user-{i+1}",
        "test_id": f"local-test-{int(time.time())}-{i}"
    }

    try:
        print(f"📤 메시지 #{i+1} 전송 중...")
        future = producer.send('s3-test-logs', test_log)
        result = future.get(timeout=10)
        print(f"✅ 메시지 #{i+1} 전송 성공! Partition: {result.partition}, Offset: {result.offset}")
        time.sleep(1)

    except Exception as e:
        print(f"❌ 메시지 #{i+1} 전송 실패: {e}")

producer.close()
print("✨ 테스트 완료!")