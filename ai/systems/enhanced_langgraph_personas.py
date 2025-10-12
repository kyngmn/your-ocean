from typing import Dict, List, Optional, TypedDict, Annotated
from langgraph.graph import StateGraph, END, START
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from gms_client import GMSClient
from ai.app.models.bert_emotion import BERTEmotionDetector
import json
from datetime import datetime
import operator
import time

class EnhancedConversationState(TypedDict):
    """강화된 대화 상태 관리"""
    user_input: str
    personality_scores: Dict[str, float]
    active_personas: List[str]
    discussion_history: Annotated[List[Dict], operator.add]
    inference_steps: Annotated[List[Dict], operator.add]
    current_round: int
    max_rounds: int
    waiting_for_user: bool
    follow_up_questions: List[str]
    summary: str
    conversation_complete: bool
    persona_interactions: Dict[str, List[str]]  # 페르소나 간 상호작용 추적

class EnhancedBig5PersonaAgent:
    """개선된 Big5 페르소나 에이전트 - 중복 방지 및 명확한 역할"""
    
    def __init__(self, gms_client: GMSClient, trait_name: str, trait_code: str):
        self.gms_client = gms_client
        self.trait_name = trait_name
        self.trait_code = trait_code
        self.character_traits = self._define_character()
        self.previous_points = []  # 이전에 한 말 기록
    
    def _define_character(self) -> Dict:
        """각 페르소나의 고유한 캐릭터와 역할 정의"""
        characters = {
            "E": {
                "name": "외향성",
                "emoji": "🌟",
                "personality": "에너지 넘치는 활동가",
                "unique_perspective": "사회적 관계와 활동성",
                "speaking_patterns": ["판을 벌이자!", "함께하면 더 강해져!", "앞에 나서는 힘이 있어!"],
                "focus_questions": ["누구와 함께할 수 있을까?", "어떻게 더 적극적으로?", "사람들과 어떻게 소통할까?"],
                "avoid_topics": ["혼자 해결하기", "내성적 접근"]
            },
            "A": {
                "name": "친화성", 
                "emoji": "🤝",
                "personality": "따뜻한 협력자",
                "unique_perspective": "관계 조화와 협력",
                "speaking_patterns": ["서로 이해해보자", "협력하면 더 좋아져", "상대방 입장에서는"],
                "focus_questions": ["어떻게 갈등을 줄일까?", "팀워크를 어떻게?", "상대방은 어떻게 느낄까?"],
                "avoid_topics": ["경쟁", "강압적 해결"]
            },
            "C": {
                "name": "성실성",
                "emoji": "📋", 
                "personality": "체계적인 실행가",
                "unique_perspective": "계획과 실행",
                "speaking_patterns": ["단계적으로 접근하자", "목표를 명확히 하고", "계획을 세워보면"],
                "focus_questions": ["구체적인 계획은?", "마감은 언제?", "우선순위는?"],
                "avoid_topics": ["즉흥적 해결", "무계획적 접근"]
            },
            "N": {
                "name": "신경성",
                "emoji": "😰",
                "personality": "신중한 감정 관리자", 
                "unique_perspective": "감정과 스트레스 관리",
                "speaking_patterns": ["감정을 먼저 인정하자", "스트레스 신호를 봐야 해", "균형이 중요해"],
                "focus_questions": ["스트레스 수준은?", "감정적으로 어때?", "버닝아웃 신호는?"],
                "avoid_topics": ["감정 무시", "무리한 추진"]
            },
            "O": {
                "name": "개방성",
                "emoji": "🎨",
                "personality": "창의적인 탐험가",
                "unique_perspective": "새로운 가능성과 창의성",
                "speaking_patterns": ["다른 방법도 있어", "새로운 관점에서", "창의적으로 접근하면"],
                "focus_questions": ["다른 가능성은?", "새로운 방법은?", "혁신적 아이디어는?"],
                "avoid_topics": ["기존 방식만", "보수적 접근"]
            }
        }
        return characters.get(self.trait_code, {})
    
    def respond(self, state: EnhancedConversationState, other_personas_said: List[str], reasoning_callback=None) -> str:
        """중복 방지하며 고유한 관점으로 응답 + 실시간 reasoning"""
        
        my_score = state["personality_scores"].get(self.trait_name, 0.0)
        activation_level = "높음" if my_score > 0.6 else "보통" if my_score > 0.3 else "낮음"
        
        character = self.character_traits
        
        # 실시간 reasoning 단계 1: 현재 상황 분석
        if reasoning_callback:
            reasoning_callback({
                "persona": f"{character['emoji']} {self.trait_code}",
                "step": "상황 분석",
                "thought": f"사용자 발언을 {character['unique_perspective']} 관점에서 분석 중...",
                "details": f"내 특성 점수: {my_score:.1%} ({activation_level})"
            })
        
        # 다른 페르소나들이 한 말 정리
        others_points = "\n".join(other_personas_said) if other_personas_said else "아직 다른 페르소나 발언 없음"
        
        # 내가 이전에 한 말들
        my_previous = "\n".join(self.previous_points) if self.previous_points else "첫 발언"
        
        # 실시간 reasoning 단계 2: 다른 페르소나 의견 검토
        if reasoning_callback:
            reasoning_callback({
                "persona": f"{character['emoji']} {self.trait_code}", 
                "step": "의견 검토",
                "thought": "다른 페르소나들의 발언을 검토해서 중복되지 않는 관점 찾는 중...",
                "details": f"검토할 발언: {len(other_personas_said)}개"
            })
        
        system_prompt = f"""당신은 '{character['name']}({self.trait_code})' 페르소나입니다.

**고유한 역할과 관점:**
- 성격: {character['personality']}
- 전문 분야: {character['unique_perspective']}
- 말하는 스타일: {', '.join(character['speaking_patterns'][:2])}
- 주요 질문 영역: {', '.join(character['focus_questions'][:2])}
- 피해야 할 주제: {', '.join(character['avoid_topics'])}

**현재 상황:**
- 사용자 발언: "{state['user_input']}"
- 내 특성 점수: {my_score:.2f} ({activation_level})

**다른 페르소나들이 이미 한 말:**
{others_points}

**내가 이전에 한 말:**
{my_previous}

**중요한 지침:**
1. 다른 페르소나와 겹치지 않는 고유한 관점 제시
2. 내 전문 분야({character['unique_perspective']})에 집중
3. 이전에 한 말과 다른 새로운 포인트
4. 점수가 높으면 적극적 조언, 낮으면 주의점 제시
5. 1-2문장으로 명확하게
6. 필요시 내 영역의 구체적 질문

**응답 형식:** "[내 관점] + [구체적 조언/질문]"

반드시 한국어로 답변하세요."""

        # 실시간 reasoning 단계 3: AI에게 질의
        if reasoning_callback:
            reasoning_callback({
                "persona": f"{character['emoji']} {self.trait_code}",
                "step": "AI 추론", 
                "thought": f"{character['name']} 관점으로 고유한 조언 생성 중...",
                "details": "GMS API 통신 중..."
            })
        
        try:
            response = self.gms_client.simple_chat(state["user_input"], system_prompt)
            response = response.strip()
            
            # 실시간 reasoning 단계 4: 응답 검증
            if reasoning_callback:
                reasoning_callback({
                    "persona": f"{character['emoji']} {self.trait_code}",
                    "step": "응답 완성",
                    "thought": f"'{response[:30]}...' 로 응답 결정",
                    "details": f"응답 길이: {len(response)}자, 고유성 확보됨"
                })
            
            # 내 발언 기록
            self.previous_points.append(response)
            if len(self.previous_points) > 3:  # 최근 3개만 유지
                self.previous_points.pop(0)
            
            return response
        except Exception as e:
            if reasoning_callback:
                reasoning_callback({
                    "persona": f"{character['emoji']} {self.trait_code}",
                    "step": "오류 처리",
                    "thought": "API 통신 오류 발생, 기본 응답으로 대체",
                    "details": f"오류: {str(e)}"
                })
            return f"[{self.trait_code}] 생각을 정리 중이에요..."

class EnhancedBig5LangGraphOrchestrator:
    """개선된 LangGraph 기반 Big5 페르소나 대화 시스템"""
    
    def __init__(self, gms_client: GMSClient, bert_detector: BERTEmotionDetector):
        self.gms_client = gms_client
        self.bert_detector = bert_detector
        
        # 페르소나 에이전트들 생성
        self.personas = {
            "외향성": EnhancedBig5PersonaAgent(gms_client, "외향성", "E"),
            "친화성": EnhancedBig5PersonaAgent(gms_client, "친화성", "A"),
            "성실성": EnhancedBig5PersonaAgent(gms_client, "성실성", "C"), 
            "신경성": EnhancedBig5PersonaAgent(gms_client, "신경성", "N"),
            "개방성": EnhancedBig5PersonaAgent(gms_client, "개방성", "O")
        }
        
        # 추론 과정 콜백
        self.inference_callback = None
        self.reasoning_callback = None  # 실시간 reasoning 콜백
        
        # LangGraph 워크플로우 구성
        self.workflow = self._build_workflow()
    
    def set_inference_callback(self, callback):
        """추론 과정 실시간 업데이트 콜백 설정"""
        self.inference_callback = callback
    
    def set_reasoning_callback(self, callback):
        """실시간 reasoning 콜백 설정"""
        self.reasoning_callback = callback
    
    def _log_inference_step(self, step_name: str, details: str, state: EnhancedConversationState):
        """추론 과정 로깅"""
        step = {
            "step": step_name,
            "details": details,
            "timestamp": datetime.now().isoformat(),
            "data": {}
        }
        
        # 콜백 호출 (실시간 UI 업데이트용)
        if self.inference_callback:
            self.inference_callback(step)
        
        return step
    
    def _build_workflow(self) -> StateGraph:
        """개선된 LangGraph 워크플로우"""
        
        workflow = StateGraph(EnhancedConversationState)
        
        # 노드들 추가
        workflow.add_node("analyze_personality", self._analyze_personality)
        workflow.add_node("select_personas", self._select_personas) 
        workflow.add_node("orchestrate_discussion", self._orchestrate_discussion)
        workflow.add_node("check_continuation", self._check_continuation)
        workflow.add_node("generate_questions", self._generate_questions)
        workflow.add_node("generate_summary", self._generate_summary)
        
        # 워크플로우 정의
        workflow.add_edge(START, "analyze_personality")
        workflow.add_edge("analyze_personality", "select_personas")
        workflow.add_edge("select_personas", "orchestrate_discussion")
        workflow.add_edge("orchestrate_discussion", "check_continuation")
        
        # 조건부 분기
        workflow.add_conditional_edges(
            "check_continuation",
            self._should_continue,
            {
                "continue": "orchestrate_discussion",
                "ask_question": "generate_questions", 
                "end": "generate_summary"
            }
        )
        
        workflow.add_edge("generate_questions", END)
        workflow.add_edge("generate_summary", END)
        
        return workflow.compile()
    
    def _analyze_personality(self, state: EnhancedConversationState) -> EnhancedConversationState:
        """BERT로 성격 분석 + 추론 과정 로깅"""
        
        step = self._log_inference_step(
            "🧠 성격 분석", 
            f"BERT 모델로 텍스트 분석 중: '{state['user_input'][:50]}...'",
            state
        )
        
        personality_scores = self.bert_detector.predict_emotion(state["user_input"])
        
        # 분석 결과 상세 로깅
        analysis_details = "Big5 성격 특성 점수:\n" + "\n".join([
            f"  {trait}: {score:.1%}" for trait, score in personality_scores.items()
        ])
        
        step["details"] = f"성격 분석 완료\n{analysis_details}"
        step["data"] = personality_scores
        
        return {
            **state,
            "personality_scores": personality_scores,
            "inference_steps": [step]
        }
    
    def _select_personas(self, state: EnhancedConversationState) -> EnhancedConversationState:
        """페르소나 선정 + 선정 이유 로깅"""
        
        scores = state["personality_scores"]
        
        # 선정 로직
        sorted_personas = sorted(scores.items(), key=lambda x: x[1], reverse=True)
        
        # 상위 점수 + 특별한 케이스 고려
        active_personas = []
        selection_reasons = []
        
        for trait, score in sorted_personas:
            if len(active_personas) < 4 and score > 0.2:
                active_personas.append(trait)
                selection_reasons.append(f"{trait}: {score:.1%} (높은 점수)")
            elif score > 0.5:  # 매우 높은 점수는 무조건 포함
                if trait not in active_personas:
                    active_personas.append(trait)
                    selection_reasons.append(f"{trait}: {score:.1%} (매우 높음)")
        
        # 최소 2개는 보장
        if len(active_personas) < 2:
            for trait, _ in sorted_personas[:2]:
                if trait not in active_personas:
                    active_personas.append(trait)
                    selection_reasons.append(f"{trait}: 기본 선정")
        
        step = self._log_inference_step(
            "🎭 페르소나 선정",
            f"활성화된 페르소나: {', '.join(active_personas)}\n\n선정 이유:\n" + "\n".join(selection_reasons),
            state
        )
        step["data"] = {"active_personas": active_personas, "reasons": selection_reasons}
        
        return {
            **state,
            "active_personas": active_personas,
            "current_round": 0,
            "max_rounds": 3,
            "persona_interactions": {persona: [] for persona in active_personas},
            "inference_steps": state["inference_steps"] + [step]
        }
    
    def _orchestrate_discussion(self, state: EnhancedConversationState) -> EnhancedConversationState:
        """순차적 페르소나 토론 - 중복 방지"""
        
        current_round = state["current_round"] + 1
        
        step = self._log_inference_step(
            f"💬 토론 라운드 {current_round}",
            f"페르소나들이 순차적으로 발언합니다...",
            state
        )
        
        new_discussions = []
        round_summary = []
        
        # 이번 라운드에서 나온 발언들 추적
        this_round_points = []
        
        for i, persona_name in enumerate(state["active_personas"]):
            persona = self.personas[persona_name]
            
            # 다른 페르소나들이 이미 한 말 (이번 라운드)
            other_personas_said = this_round_points.copy()
            
            # 페르소나 응답 생성 - 실시간 reasoning
            response = persona.respond(state, other_personas_said, self.reasoning_callback)
            
            discussion_entry = {
                "persona_name": persona_name,
                "persona_code": persona.trait_code,
                "emoji": persona.character_traits.get("emoji", "🤖"),
                "response": response,
                "round": current_round,
                "order": i + 1,
                "timestamp": datetime.now().isoformat()
            }
            
            new_discussions.append(discussion_entry)
            this_round_points.append(f"{persona.trait_code}: {response}")
            round_summary.append(f"{persona.character_traits.get('emoji', '🤖')} {persona.trait_code}: {response}")
        
        # 라운드 요약 업데이트
        step["details"] = f"라운드 {current_round} 완료\n\n" + "\n".join(round_summary)
        step["data"] = {"discussions": new_discussions}
        
        return {
            **state,
            "discussion_history": state.get("discussion_history", []) + new_discussions,
            "current_round": current_round,
            "inference_steps": state["inference_steps"] + [step]
        }
    
    def _check_continuation(self, state: EnhancedConversationState) -> EnhancedConversationState:
        """대화 계속 여부 판단"""
        
        continue_decision = state["current_round"] < state["max_rounds"]
        
        step = self._log_inference_step(
            "🤔 대화 흐름 판단",
            f"현재 라운드: {state['current_round']}/{state['max_rounds']}\n"
            f"판단: {'계속 토론' if continue_decision else '토론 마무리'}",
            state
        )
        
        return {
            **state,
            "conversation_complete": not continue_decision,
            "inference_steps": state["inference_steps"] + [step]
        }
    
    def _should_continue(self, state: EnhancedConversationState) -> str:
        """조건부 분기 결정"""
        
        if state["conversation_complete"]:
            # 추가 질문 필요 여부 판단
            last_discussions = state["discussion_history"][-len(state["active_personas"]):]
            has_questions = any("?" in d["response"] for d in last_discussions)
            
            if has_questions and state["current_round"] < 2:
                return "ask_question"
            else:
                return "end"
        else:
            return "continue"
    
    def _generate_questions(self, state: EnhancedConversationState) -> EnhancedConversationState:
        """추가 질문 생성"""
        
        step = self._log_inference_step(
            "❓ 추가 질문 생성",
            "페르소나들의 질문을 정리 중...",
            state
        )
        
        last_discussions = state["discussion_history"][-len(state["active_personas"]):]
        questions = []
        
        for discussion in last_discussions:
            if "?" in discussion["response"]:
                sentences = discussion["response"].split(".")
                for sentence in sentences:
                    if "?" in sentence:
                        clean_question = sentence.strip()
                        if clean_question and clean_question not in questions:
                            questions.append(clean_question)
        
        step["details"] = f"추출된 질문들:\n" + "\n".join(questions) if questions else "추가 질문 없음"
        step["data"] = {"questions": questions}
        
        return {
            **state,
            "follow_up_questions": questions,
            "waiting_for_user": True,
            "inference_steps": state["inference_steps"] + [step]
        }
    
    def _generate_summary(self, state: EnhancedConversationState) -> EnhancedConversationState:
        """토론 요약 생성"""
        
        step = self._log_inference_step(
            "📋 토론 요약 생성",
            "AI가 페르소나 토론을 종합 분석 중...",
            state
        )
        
        # 페르소나별 주요 포인트 정리
        persona_points = {}
        for discussion in state["discussion_history"]:
            persona = discussion["persona_code"]
            if persona not in persona_points:
                persona_points[persona] = []
            persona_points[persona].append(discussion["response"])
        
        summary_context = "\n".join([
            f"{persona}: {'; '.join(points)}" 
            for persona, points in persona_points.items()
        ])
        
        summary_prompt = f"""다음은 Big5 페르소나들의 토론 결과입니다.

**사용자:** "{state['user_input']}"

**성격 분석:**
{json.dumps(state['personality_scores'], indent=2, ensure_ascii=False)}

**페르소나별 주요 포인트:**
{summary_context}

**요약 요청:**
위 토론을 바탕으로 다음 형식으로 명확하게 정리해주세요:

**💡 당신의 강점**
- (구체적인 강점 2-3개)

**⚠️ 주의할 점**
- (개선이 필요한 부분 1-2개)

**🎯 실행 제안**  
- (바로 실행 가능한 구체적 방안 2-3개)

각 항목은 간결하고 실용적으로 작성해주세요."""

        try:
            summary = self.gms_client.simple_chat("", summary_prompt)
            step["details"] = "요약 완료"
            step["data"] = {"summary": summary}
        except Exception as e:
            summary = "요약 생성 중 오류가 발생했습니다."
            step["details"] = f"요약 생성 실패: {str(e)}"
        
        return {
            **state,
            "summary": summary,
            "inference_steps": state["inference_steps"] + [step]
        }
    
    def start_conversation(self, user_input: str, progress_callback=None, reasoning_callback=None) -> Dict:
        """대화 시작 - 실시간 진행상황 + reasoning 콜백 지원"""
        
        if progress_callback:
            self.set_inference_callback(progress_callback)
        if reasoning_callback:
            self.set_reasoning_callback(reasoning_callback)
        
        initial_state = EnhancedConversationState(
            user_input=user_input,
            personality_scores={},
            active_personas=[],
            discussion_history=[],
            inference_steps=[],
            current_round=0,
            max_rounds=3,
            waiting_for_user=False,
            follow_up_questions=[],
            summary="",
            conversation_complete=False,
            persona_interactions={}
        )
        
        # 워크플로우 실행
        result = self.workflow.invoke(initial_state)
        
        return result

# 테스트 함수
def test_enhanced_system():
    """개선된 시스템 테스트"""
    
    def progress_callback(step):
        """진행상황 출력"""
        print(f"⚡ {step['step']}: {step['details']}")
        if step.get('data'):
            print(f"   데이터: {step['data']}")
        print()
    
    try:
        print("🚀 개선된 LangGraph 페르소나 시스템 테스트")
        print("=" * 70)
        
        gms_client = GMSClient()
        bert_detector = BERTEmotionDetector()
        orchestrator = EnhancedBig5LangGraphOrchestrator(gms_client, bert_detector)
        
        test_input = "요즘 다 내가 떠안는 것 같아. 밀어붙이면 되긴 하는데 애들이 눈치만 봐"
        
        result = orchestrator.start_conversation(test_input, progress_callback)
        
        print("=" * 70)
        print("📊 최종 결과:")
        print(f"활성화된 페르소나: {result['active_personas']}")
        print(f"총 추론 단계: {len(result['inference_steps'])}")
        print(f"토론 라운드: {result['current_round']}")
        print(f"\n📋 요약:")
        print(result['summary'])
        
    except Exception as e:
        print(f"❌ 테스트 오류: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    test_enhanced_system()