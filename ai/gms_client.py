import requests
import json
import os
from typing import Dict, List, Optional
import time
import asyncio
import aiohttp
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

class GMSClient:
    """OpenAI API 클라이언트"""

    def __init__(self, api_key: Optional[str] = None):
        """
        OpenAI 클라이언트 초기화

        Args:
            api_key: OpenAI API 키 (없으면 환경변수에서 가져옴)
        """
        self.api_key = api_key or os.getenv('OPENAI_API_KEY')
        self.base_url = "https://api.openai.com/v1/chat/completions"

        if not self.api_key:
            raise ValueError("OPENAI_API_KEY 환경변수를 설정하거나 api_key를 제공해주세요!")
    
    def chat_completion(
        self,
        messages: List[Dict[str, str]],
        max_completion_tokens: int = 500
    ) -> str:
        """
        OpenAI GPT로 채팅 완성

        Args:
            messages: 메시지 리스트 [{"role": "user", "content": "..."}]
            max_completion_tokens: 최대 출력 토큰 수

        Returns:
            생성된 응답 텍스트
        """
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.api_key}"
        }

        data = {
            "model": "gpt-4o-mini",
            "messages": messages,
            "max_completion_tokens": max_completion_tokens
        }
        
        try:
            response = requests.post(
                self.base_url,
                headers=headers,
                json=data,
                timeout=15  # 타임아웃 줄임
            )
            response.raise_for_status()
            
            result = response.json()
            return result['choices'][0]['message']['content']
            
        except requests.exceptions.RequestException as e:
            print(f"❌ API 요청 오류: {e}")
            return f"죄송합니다. API 요청 중 오류가 발생했습니다: {str(e)}"
        except KeyError as e:
            print(f"❌ 응답 파싱 오류: {e}")
            return "죄송합니다. 응답을 해석할 수 없습니다."
    
    def simple_chat(self, user_message: str, system_prompt: str = "") -> str:
        """간단한 채팅 인터페이스"""
        messages = []
        
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        
        messages.append({"role": "user", "content": user_message})
        
        return self.chat_completion(messages)
    
    async def chat_completion_async(
        self,
        messages: List[Dict[str, str]],
        max_completion_tokens: int = 500
    ) -> str:
        """
        OpenAI GPT로 비동기 채팅 완성

        Args:
            messages: 메시지 리스트 [{"role": "user", "content": "..."}]
            max_completion_tokens: 최대 출력 토큰 수

        Returns:
            생성된 응답 텍스트
        """
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.api_key}"
        }

        data = {
            "model": "gpt-4o-mini",
            "messages": messages,
            "max_completion_tokens": max_completion_tokens
        }
        
        try:
            timeout = aiohttp.ClientTimeout(total=15)
            async with aiohttp.ClientSession(timeout=timeout) as session:
                async with session.post(
                    self.base_url,
                    headers=headers,
                    json=data
                ) as response:
                    response.raise_for_status()
                    result = await response.json()
                    return result['choices'][0]['message']['content']
                    
        except aiohttp.ClientError as e:
            print(f"❌ 비동기 API 요청 오류: {e}")
            return f"죄송합니다. API 요청 중 오류가 발생했습니다: {str(e)}"
        except KeyError as e:
            print(f"❌ 응답 파싱 오류: {e}")
            return "죄송합니다. 응답을 해석할 수 없습니다."
        except Exception as e:
            print(f"❌ 예상치 못한 오류: {e}")
            return f"죄송합니다. 예상치 못한 오류가 발생했습니다: {str(e)}"

# 테스트용 함수
def test_gms_client():
    """OpenAI 클라이언트 테스트"""
    try:
        client = GMSClient()

        test_message = "안녕하세요! OpenAI API 테스트입니다."
        system_prompt = "당신은 친근한 AI 어시스턴트입니다. 한국어로 답변해주세요."

        print("🤖 OpenAI GPT 테스트")
        print(f"👤 사용자: {test_message}")
        print("-" * 50)

        response = client.simple_chat(test_message, system_prompt)
        print(f"🤖 AI: {response}")

        return True

    except ValueError as e:
        print(f"⚠️ 설정 오류: {e}")
        print("💡 OPENAI_API_KEY 환경변수를 설정해주세요!")
        return False

if __name__ == "__main__":
    test_gms_client()