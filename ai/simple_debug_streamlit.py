import streamlit as st
import asyncio
import logging
import sys
from reasoning_persona_system import ReasoningPersonaSystem

# 로깅 설정
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(levelname)s - %(message)s',
    stream=sys.stdout
)
logger = logging.getLogger(__name__)

def main():
    st.title("🔍 디버그 테스트")
    
    if 'system' not in st.session_state:
        st.session_state.system = None
    
    user_input = st.text_input("테스트 입력:", "새로운 직장에 적응하기 어려워요")
    
    if st.button("테스트 실행"):
        if not user_input:
            st.warning("입력을 해주세요!")
            return
            
        try:
            st.info("🔄 시스템 초기화 중...")
            logger.info("시스템 초기화 시작")
            
            if st.session_state.system is None:
                st.session_state.system = ReasoningPersonaSystem()
                logger.info("시스템 초기화 완료")
            
            st.info("🔄 API 호출 중...")
            logger.info("API 호출 시작")
            
            # 간단한 테스트 점수
            test_scores = {
                "Extraversion": 0.7,
                "Agreeableness": 0.6,
                "Conscientiousness": 0.8,
                "Neuroticism": 0.4,
                "Openness": 0.7
            }
            
            logger.info(f"테스트 점수: {test_scores}")
            
            # 타임아웃과 함께 실행
            async def run_test():
                logger.info("추론 시작")
                result = await st.session_state.system.process_conversation(user_input, test_scores)
                logger.info(f"추론 완료: {type(result)}")
                return result
            
            result = asyncio.run(run_test())
            
            st.success("✅ 완료!")
            
            # 결과 상세 표시
            st.write(f"**결과 타입:** {type(result)}")
            
            if isinstance(result, dict):
                st.write(f"**결과 키:** {list(result.keys())}")
                
                for key, value in result.items():
                    st.write(f"**{key}:** {type(value)} - {str(value)[:100]}...")
                    
                st.json(result)
            else:
                st.write(f"**결과 값:** {result}")
            
        except Exception as e:
            logger.error(f"오류 발생: {e}")
            import traceback
            st.error(f"오류: {e}")
            st.code(traceback.format_exc())

if __name__ == "__main__":
    main()