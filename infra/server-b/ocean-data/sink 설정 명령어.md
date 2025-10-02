curl -X POST http://localhost:8084/connectors \
    -H "Content-Type: application/json" \
    -d '{
      "name": "s3-test-logs-sink",
      "config": {
        "connector.class": "io.confluent.connect.s3.S3SinkConnector",
        "topics": "s3-test-logs",
        "s3.bucket.name": "raw",
        "s3.region": "ap-northeast-2",
        "store.url": "http://minio1:9000",
        "aws.access.key.id": "hellopong",
        "aws.secret.access.key": "hellopong303",
        "format.class": "io.confluent.connect.s3.format.bytearray.ByteArrayFormat",
        "flush.size": "10",
        "rotate.interval.ms": "10000",
        "storage.class": "io.confluent.connect.s3.storage.S3Storage",
        "s3.path.style.access.enabled": "true",
        "value.converter": "org.apache.kafka.connect.converters.ByteArrayConverter",
        "key.converter": "org.apache.kafka.connect.storage.StringConverter"
      }
    }'


data.myocean.cloud:9094  # 또는 172.26.14.91:9094

백엔드에서 사용할 연결 정보:
producer = KafkaProducer(
bootstrap_servers=['data.myocean.cloud:9094'],
value_serializer=lambda v: json.dumps(v).encode('utf-8')
)