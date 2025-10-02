import asyncio
from gms_client import GMSClient
import logging

logging.basicConfig(level=logging.DEBUG)

async def test_simple():
    """간단한 API 테스트"""
    try:
        print("🔄 GMS API 테스트 시작...")
        client = GMSClient()
        
        # 매우 간단한 테스트
        messages = [
            {"role": "system", "content": "답을 짧게"},
            {"role": "user", "content": "안녕"}
        ]
        
        print("📡 API 호출 중...")
        response = await client.chat_completion_async(messages, max_completion_tokens=20)
        
        print(f"✅ 응답: '{response}'")
        print(f"📏 길이: {len(response)}")
        
        if response and response.strip():
            print("✅ API 정상 작동!")
            return True
        else:
            print("⚠️ 빈 응답 받음")
            return False
            
    except Exception as e:
        print(f"❌ 오류: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = asyncio.run(test_simple())
    exit(0 if success else 1)