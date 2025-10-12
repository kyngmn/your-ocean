import streamlit as st
import asyncio
import json
import logging
import sys
from ai.systems.reasoning_persona_system import ReasoningPersonaSystem
from ai.app.models.bert_emotion import BERTEmotionDetector

# 로깅 설정
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    stream=sys.stdout
)
logger = logging.getLogger(__name__)

def main():
    st.set_page_config(
        page_title="Big5 추론 페르소나 시스템",
        page_icon="🧠", 
        layout="wide"
    )
    
    st.title("🧠 Big5 추론 페르소나 시스템")
    st.markdown("Big5 성격 점수를 입력하면, Domain Agent가 분류하고 5개의 성격 에이전트가 LangGraph로 추론하며 대화합니다!")
    
    # 세션 상태 초기화
    if 'reasoning_system' not in st.session_state:
        st.session_state.reasoning_system = None
    if 'bert_detector' not in st.session_state:
        st.session_state.bert_detector = BERTEmotionDetector()
    if 'conversation_history' not in st.session_state:
        st.session_state.conversation_history = []
    if 'live_updates' not in st.session_state:
        st.session_state.live_updates = []
    if 'is_processing' not in st.session_state:
        st.session_state.is_processing = False
    
    # 사이드바: Big5 점수 입력
    with st.sidebar:
        st.markdown("### 🎯 Big5 성격 점수 입력")
        st.markdown("각 성격 요인의 점수를 0.0~1.0 사이로 입력하세요")
        
        # Big5 점수 입력
        big5_scores = {}
        trait_info = {
            "Extraversion": {"emoji": "🌟", "name": "외향성", "desc": "활발함, 사교성"},
            "Agreeableness": {"emoji": "🤝", "name": "친화성", "desc": "협조성, 공감능력"},
            "Conscientiousness": {"emoji": "📋", "name": "성실성", "desc": "계획성, 책임감"},
            "Neuroticism": {"emoji": "🤔", "name": "신경성", "desc": "감정기복, 스트레스 민감도"},
            "Openness": {"emoji": "🎨", "name": "개방성", "desc": "창의성, 호기심"}
        }
        
        for trait, info in trait_info.items():
            big5_scores[trait] = st.slider(
                f"{info['emoji']} {info['name']} ({info['desc']})",
                min_value=0.0,
                max_value=1.0,
                value=0.5,
                step=0.1,
                key=f"score_{trait}"
            )
        
        st.markdown("---")
        
        # BERT 자동 분석 옵션
        st.markdown("### 🤖 BERT 자동 분석")
        if st.button("📝 입력 텍스트로 Big5 점수 자동 계산"):
            if 'current_input' in st.session_state and st.session_state.current_input:
                with st.spinner("BERT 모델로 성격 분석 중..."):
                    try:
                        bert_scores = st.session_state.bert_detector.predict(st.session_state.current_input)
                        for trait in big5_scores.keys():
                            if trait in bert_scores:
                                st.session_state[f"score_{trait}"] = bert_scores[trait]
                        st.success("BERT 분석 완료! 점수가 자동 업데이트되었습니다.")
                        st.rerun()
                    except Exception as e:
                        st.error(f"BERT 분석 오류: {e}")
            else:
                st.warning("먼저 메인 화면에서 텍스트를 입력해주세요!")
    
    # 메인 화면
    col1, col2 = st.columns([3, 1])
    
    with col1:
        user_input = st.text_area(
            "💬 무엇이든 물어보세요! (일기, 고민, 질문 등)",
            height=120,
            placeholder="예: 새로운 직장에서 적응하기 어려워요. 동료들과 어떻게 친해져야 할까요?",
            key="user_input"
        )
        st.session_state.current_input = user_input
    
    with col2:
        st.markdown("<br>", unsafe_allow_html=True)
        if st.button("🚀 AI 페르소나들과 대화 시작", use_container_width=True, type="primary", disabled=st.session_state.is_processing):
            if user_input.strip() and not st.session_state.is_processing:
                # 현재 사이드바의 Big5 점수 가져오기
                current_scores = {trait: st.session_state[f"score_{trait}"] for trait in big5_scores.keys()}
                
                # 처리 상태 시작
                st.session_state.is_processing = True
                st.session_state.live_updates = []
                
                # 실시간 콜백 함수
                def live_callback(message):
                    st.session_state.live_updates.append(message)
                
                # 추론 시스템 초기화 (콜백 포함)
                try:
                    if st.session_state.reasoning_system is None:
                        st.session_state.reasoning_system = ReasoningPersonaSystem(callback=live_callback)
                    else:
                        st.session_state.reasoning_system.callback = live_callback
                except Exception as init_error:
                    st.error(f"시스템 초기화 오류: {init_error}")
                    st.stop()
                
                try:
                    # 실시간 업데이트 표시
                    progress_container = st.container()
                    with progress_container:
                        st.info("🚀 추론을 시작합니다...")
                    
                    logger.info(f"추론 시작 - 사용자 입력: {user_input[:50]}...")
                    logger.info(f"Big5 점수: {current_scores}")
                    
                    # 추론 시스템 실행 (타임아웃 추가)
                    async def run_with_timeout():
                        return await asyncio.wait_for(
                            st.session_state.reasoning_system.process_conversation(user_input, current_scores),
                            timeout=60.0  # 60초 타임아웃
                        )
                    
                    try:
                        result = asyncio.run(run_with_timeout())
                        logger.info(f"추론 완료! 결과 타입: {type(result)}")
                        logger.info(f"결과 키: {list(result.keys()) if isinstance(result, dict) else 'Not a dict'}")
                        
                        # 결과 검증
                        if not result:
                            st.error("❌ 빈 결과를 받았습니다.")
                            st.session_state.is_processing = False
                            return
                            
                    except asyncio.TimeoutError:
                        logger.error("추론 타임아웃!")
                        st.error("⏰ 시간이 초과되었습니다. 다시 시도해주세요.")
                        st.session_state.is_processing = False
                        return
                    except Exception as api_error:
                        logger.error(f"API 호출 중 오류: {api_error}")
                        st.error(f"❌ API 호출 오류: {api_error}")
                        st.session_state.is_processing = False
                        return
                    
                    # 처리 완료
                    st.session_state.is_processing = False
                    
                    # 결과 즉시 표시
                    progress_container.empty()  # 기존 진행 메시지 지우기
                    
                    st.success("✅ AI 추론 완료!")
                    
                    # 도메인 분류 결과 표시
                    st.info(f"🎯 도메인 분류: **{result.get('domain_classification', 'Unknown')}**")
                    
                    # 각 에이전트 응답 미리보기
                    agent_responses = result.get('agent_responses', {})
                    if agent_responses:
                        st.markdown("### 🤖 각 AI 에이전트 응답")
                        
                        cols = st.columns(len(agent_responses))
                        for i, (agent, response) in enumerate(agent_responses.items()):
                            with cols[i]:
                                trait_emoji = trait_info.get(agent, {}).get('emoji', '🤖')
                                trait_name = trait_info.get(agent, {}).get('name', agent)
                                st.markdown(f"**{trait_emoji} {trait_name}**")
                                st.write(response[:200] + ('...' if len(response) > 200 else ''))
                    
                    # 최종 결론
                    final_conclusion = result.get('final_conclusion', '')
                    if final_conclusion:
                        st.markdown("### 🎯 최종 종합 조언")
                        st.success(final_conclusion)
                    
                    # API 실패 여부 확인
                    if result.get('domain_classification') == "ERROR":
                        st.error("❌ 시스템 오류가 발생했습니다. 나중에 다시 시도해주세요.")
                    elif "API 호출 실패가 다수 발생" in result.get('final_conclusion', ''):
                        st.warning("⚠️ API 호출에 문제가 있습니다. 네트워크 상태를 확인하고 다시 시도해주세요.")
                    
                    # 대화 기록 저장 (실패한 경우에도 기록)
                    st.session_state.conversation_history.append({
                        'user_input': user_input,
                        'big5_scores': current_scores.copy(),
                        'result': result,
                        'live_updates': st.session_state.live_updates.copy()
                    })
                    
                    st.rerun()
                    
                except Exception as e:
                    st.session_state.is_processing = False
                    st.error(f"❌ 치명적 오류가 발생했습니다: {e}")
                    st.info("💡 API 키가 올바른지 확인하고 다시 시도해주세요.")
            elif st.session_state.is_processing:
                st.warning("⏳ 현재 처리 중입니다. 잠시만 기다려주세요...")
            else:
                st.warning("텍스트를 입력해주세요!")
        
        if st.button("🗑️ 대화 기록 지우기", use_container_width=True):
            st.session_state.conversation_history = []
            st.rerun()
    
    # 실시간 처리 상태 표시
    if st.session_state.is_processing and st.session_state.live_updates:
        st.markdown("### 🔄 실시간 추론 과정")
        live_container = st.container()
        with live_container:
            for i, update in enumerate(st.session_state.live_updates):
                if i == len(st.session_state.live_updates) - 1:
                    # 최신 업데이트는 하이라이트
                    st.info(f"**{update}**")
                else:
                    st.write(f"✓ {update}")
    
    # 대화 기록 표시
    if st.session_state.conversation_history:
        st.markdown("---")
        st.markdown("## 📜 대화 기록")
        
        for i, conversation in enumerate(reversed(st.session_state.conversation_history)):
            with st.container():
                st.markdown(f"### 💬 대화 #{len(st.session_state.conversation_history) - i}")
                
                # 사용자 입력
                st.markdown(f"**👤 사용자:** {conversation['user_input']}")
                
                # Big5 점수 표시
                st.markdown("**📊 사용된 Big5 점수:**")
                score_cols = st.columns(5)
                for j, (trait, score) in enumerate(conversation['big5_scores'].items()):
                    with score_cols[j]:
                        info = trait_info[trait]
                        st.metric(
                            label=f"{info['emoji']} {info['name']}",
                            value=f"{score:.1f}"
                        )
                
                result = conversation['result']
                
                # 도메인 분류 결과
                st.markdown(f"**🎯 Domain Agent 분류:** `{result['domain_classification']}`")
                
                # 추론 과정 표시
                st.markdown("**🔄 AI 추론 과정:**")
                
                # 실시간 업데이트 기록 표시 (있는 경우)
                if 'live_updates' in conversation and conversation['live_updates']:
                    live_expander = st.expander("실시간 추론 과정 보기", expanded=False)
                    with live_expander:
                        for update in conversation['live_updates']:
                            if "분석 중" in update or "중..." in update:
                                st.info(f"🔄 {update}")
                            elif "완료" in update:
                                st.success(f"✅ {update}")
                            elif "오류" in update or "실패" in update:
                                st.error(f"❌ {update}")
                            else:
                                st.write(f"📝 {update}")
                
                reasoning_expander = st.expander("추론 단계별 과정 보기", expanded=False)
                with reasoning_expander:
                    for step in result['reasoning_chain']:
                        if 'action' in step:
                            st.info(f"**{step['agent']}**: {step['action']}")
                        else:
                            agent_info = trait_info.get(step['agent'], {"emoji": "🤖", "name": step['agent']})
                            st.write(f"**{agent_info['emoji']} {agent_info['name']}**: {step['response']}")
                
                # 각 에이전트 응답 표시
                st.markdown("**🤖 각 Big5 Agent 응답:**")
                
                agent_responses = result.get('agent_responses', {})
                if agent_responses:
                    agent_cols = st.columns(len(agent_responses))
                    
                    for idx, (trait, response) in enumerate(agent_responses.items()):
                        with agent_cols[idx]:
                            info = trait_info.get(trait, {"emoji": "🤖", "name": trait})
                            score = conversation['big5_scores'].get(trait, 0.0)
                            
                            st.markdown(f"**{info['emoji']} {info['name']}**")
                            st.markdown(f"*점수: {score:.1f}*")
                            if response:
                                st.write(response)
                            else:
                                st.write("응답 없음")
                
                # 최종 종합
                final_conclusion = result.get('final_conclusion', '')
                if final_conclusion:
                    st.markdown("**🎯 최종 종합 조언:**")
                    st.success(final_conclusion)
                else:
                    st.warning("최종 종합 조언이 생성되지 않았습니다.")
                
                if i < len(st.session_state.conversation_history) - 1:
                    st.markdown("---")
    
    # 하단 정보
    st.markdown("---")
    with st.expander("ℹ️ 시스템 사용 방법", expanded=False):
        st.markdown("""
        ### 🔄 추론 과정
        1. **Domain Agent**: 사용자 입력을 5가지 도메인(관계/진로/생활/감정/결정)으로 분류
        2. **Big5 Agents**: 외향성 → 친화성 → 성실성 → 신경성 → 개방성 순으로 각자의 관점에서 응답
        3. **LangGraph**: 각 에이전트가 이전 응답들을 참고하여 순차적으로 추론
        4. **최종 종합**: 모든 관점을 종합하여 핵심 조언 제시
        
        ### 📊 Big5 성격 요인
        - **외향성**: 활발함, 사교성, 에너지
        - **친화성**: 협조성, 공감능력, 친화력  
        - **성실성**: 계획성, 책임감, 목표지향
        - **신경성**: 감정기복, 스트레스 민감도
        - **개방성**: 창의성, 호기심, 새로운 경험 추구
        
        ### 🎯 특징
        - 외부 Big5 점수 입력 지원
        - BERT 자동 분석 옵션  
        - LangGraph 기반 순차적 추론
        - 각 에이전트가 2-3문장으로 간결한 응답
        - 개인 성격 점수에 따른 맞춤형 조언
        """)

if __name__ == "__main__":
    main()