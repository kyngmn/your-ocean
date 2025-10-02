import asyncio
import logging
from typing import Dict, List, TypedDict, Annotated
from langgraph.graph import StateGraph, END
import operator
from gms_client import GMSClient
from bert_emotion_detector import BERTEmotionDetector

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class AgentState(TypedDict):
    messages: Annotated[List[str], operator.add]
    user_input: str
    big5_scores: Dict[str, float]
    domain_classification: str
    agent_responses: Dict[str, str]
    reasoning_chain: List[Dict[str, str]]
    final_conclusion: str

class DomainAgent:
    """도메인 분류 및 라우팅을 담당하는 에이전트"""
    
    def __init__(self, gms_client: GMSClient):
        self.gms_client = gms_client
        
    async def classify_domain(self, user_input: str) -> str:
        """사용자 입력을 도메인별로 분류"""
        system_prompt = """
        사용자의 입력을 Big5 성격 요인 중 가장 관련 깊은 도메인으로 분류하세요:
        
        1. EXTRAVERSION - 사회적 상호작용, 활발함, 에너지, 외향적 행동 관련
        2. AGREEABLENESS - 협력, 공감, 친화력, 타인 배려 관련  
        3. CONSCIENTIOUSNESS - 계획성, 책임감, 목표달성, 성실함 관련
        4. NEUROTICISM - 감정기복, 스트레스, 불안, 정서적 안정성 관련
        5. OPENNESS - 창의성, 새로운 경험, 호기심, 개방적 사고 관련
        
        정확히 하나의 Big5 요인명만 응답하세요.
        """
        
        try:
            response = await self.gms_client.chat_completion_async(
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": user_input}
                ],
                max_completion_tokens=20
            )
            return response.strip()
        except Exception as e:
            logger.error(f"도메인 분류 API 호출 실패: {e}")
            # API 실패 시 기본값 반환하고 더 이상 호출하지 않음
            return "EMOTIONAL"

class Big5Agent:
    """Big5 성격 요인별 에이전트"""
    
    def __init__(self, trait: str, gms_client: GMSClient):
        self.trait = trait
        self.gms_client = gms_client
        
        self.trait_configs = {
            "Extraversion": {
                "name": "에너지 넘치는 소셜러",
                "personality": "외향적이고 활발하며 사교적입니다. 새로운 사람들과의 만남을 즐기고 에너지가 넘칩니다.",
                "thinking_style": "적극적이고 행동지향적으로 생각하며, 사회적 상호작용을 통한 해결책을 선호합니다.",
                "emoji": "🌟"
            },
            "Agreeableness": {
                "name": "따뜻한 공감러", 
                "personality": "협조적이고 친화적이며 타인을 배려합니다. 조화와 평화를 중시하고 공감능력이 뛰어납니다.",
                "thinking_style": "타인의 입장을 고려하며, 갈등을 피하고 협력적인 해결책을 찾으려 합니다.",
                "emoji": "🤝"
            },
            "Conscientiousness": {
                "name": "계획적인 실행가",
                "personality": "성실하고 책임감이 강하며 계획적입니다. 목표달성을 위해 체계적으로 접근합니다.", 
                "thinking_style": "논리적이고 단계적으로 분석하며, 구체적이고 실행가능한 계획을 세웁니다.",
                "emoji": "📋"
            },
            "Neuroticism": {
                "name": "신중한 걱정러",
                "personality": "감정이 풍부하고 세심하며 신중합니다. 잠재적 위험이나 문제를 미리 고려합니다.",
                "thinking_style": "신중하게 위험요소를 분석하고, 감정적 측면과 예상되는 어려움을 고려합니다.",
                "emoji": "🤔"
            },
            "Openness": {
                "name": "창의적인 탐험가", 
                "personality": "개방적이고 창의적이며 호기심이 많습니다. 새로운 경험과 아이디어를 추구합니다.",
                "thinking_style": "창의적이고 혁신적으로 접근하며, 기존 틀을 벗어난 독창적인 해결책을 모색합니다.",
                "emoji": "🎨"
            }
        }
    
    async def generate_response(self, state: AgentState) -> str:
        """개별 에이전트의 응답 생성"""
        config = self.trait_configs[self.trait]
        score = state["big5_scores"].get(self.trait, 0.5)
        domain = state["domain_classification"]
        
        # 점수에 따른 맞춤형 조언 스타일
        if score > 0.7:
            intensity = "매우 강하게"
            score_desc = "높은"
            personal_advice = f"당신은 {self.trait}가 매우 높은 편이므로"
        elif score > 0.5:
            intensity = "보통으로"
            score_desc = "보통의"
            personal_advice = f"당신은 {self.trait}가 보통 수준이므로"
        else:
            intensity = "약하게"
            score_desc = "낮은"
            personal_advice = f"당신은 {self.trait}가 낮은 편이므로"
            
        system_prompt = f"""
        당신은 {config['name']}입니다. ({config['emoji']})
        
        IMPORTANT: 사용자의 {self.trait} 점수는 {score:.2f}({score_desc})입니다.
        
        {personal_advice}, 다음과 같이 맞춤형 조언하세요:
        
        성격특성: {config['personality']}
        사고방식: {config['thinking_style']}
        
        조언 방식:
        1. 사용자의 {self.trait} {score:.2f} 점수를 직접 언급하며 시작
        2. 이 점수가 현재 상황에 어떤 장점/단점을 가져올지 분석
        3. 개인 성격에 맞는 구체적인 실행 방법 제시
        4. 다른 에이전트 의견에 동의하거나 강하게 반박하며 자신만의 관점 주장
        5. 정확히 2-3문장으로 응답 (절대 넘지 말 것)
        6. 당신의 {self.trait} 성격 역할에만 100% 집중하여 응답
        
        CRITICAL: 다른 에이전트들과 의견 충돌을 두려워하지 말고, 당신의 성격 특성을 강하게 드러내며 필요시 반박하세요.
        """
        
        # 이전 에이전트들의 응답 포함
        messages = [{"role": "system", "content": system_prompt}]
        
        if state["agent_responses"]:
            previous_responses = "\n".join([
                f"[{agent}]: {response}" 
                for agent, response in state["agent_responses"].items()
            ])
            messages.append({
                "role": "user", 
                "content": f"""사용자 질문: {state['user_input']}

이전 에이전트들의 응답:
{previous_responses}

위 응답들을 검토하고, 당신의 {self.trait} 관점에서:
- 동의한다면 왜 동의하는지
- 반대한다면 왜 잘못되었는지 
- 당신만의 독특한 해결책은 무엇인지

강하게 주장하며 응답해주세요 (2-3문장만):"""
            })
        else:
            messages.append({
                "role": "user",
                "content": f"사용자 질문: {state['user_input']}\n\n당신의 {self.trait} 관점에서 강하게 주장하며 응답해주세요 (2-3문장만):"
            })
        
        try:
            response = await self.gms_client.chat_completion_async(
                messages=messages,
                max_completion_tokens=80
            )
            return response.strip()
        except Exception as e:
            logger.error(f"{self.trait} 에이전트 API 호출 실패: {e}")
            # API 실패 시 더 이상 호출하지 않고 기본 응답 반환
            return f"API 호출에 실패하여 {config['name']}의 응답을 생성할 수 없습니다."

class ReasoningPersonaSystem:
    """LangGraph 기반 추론 페르소나 시스템"""
    
    def __init__(self, callback=None):
        self.gms_client = GMSClient()
        self.domain_agent = DomainAgent(self.gms_client)
        self.big5_agents = {
            trait: Big5Agent(trait, self.gms_client) 
            for trait in ["Extraversion", "Agreeableness", "Conscientiousness", "Neuroticism", "Openness"]
        }
        self.callback = callback  # 실시간 업데이트 콜백
        self.graph = self._build_graph()
    
    def _build_graph(self) -> StateGraph:
        """LangGraph 워크플로우 구성"""
        workflow = StateGraph(AgentState)
        
        # 노드 추가
        workflow.add_node("domain_classification", self._classify_domain_node)
        workflow.add_node("extraversion_agent", self._extraversion_node)
        workflow.add_node("agreeableness_agent", self._agreeableness_node) 
        workflow.add_node("conscientiousness_agent", self._conscientiousness_node)
        workflow.add_node("neuroticism_agent", self._neuroticism_node)
        workflow.add_node("openness_agent", self._openness_node)
        workflow.add_node("final_synthesis", self._synthesis_node)
        
        # 엣지 연결 (순차적 추론)
        workflow.set_entry_point("domain_classification")
        workflow.add_edge("domain_classification", "extraversion_agent")
        workflow.add_edge("extraversion_agent", "agreeableness_agent")
        workflow.add_edge("agreeableness_agent", "conscientiousness_agent") 
        workflow.add_edge("conscientiousness_agent", "neuroticism_agent")
        workflow.add_edge("neuroticism_agent", "openness_agent")
        workflow.add_edge("openness_agent", "final_synthesis")
        workflow.add_edge("final_synthesis", END)
        
        return workflow.compile()
    
    async def _classify_domain_node(self, state: AgentState) -> AgentState:
        """도메인 분류 노드"""
        if self.callback:
            self.callback("🎯 Domain Agent가 사용자 입력을 분석 중...")
        
        domain = await self.domain_agent.classify_domain(state["user_input"])
        state["domain_classification"] = domain
        state["reasoning_chain"].append({
            "agent": "DomainAgent",
            "action": f"도메인 분류: {domain}"
        })
        
        if self.callback:
            self.callback(f"✅ 도메인 분류 완료: {domain}")
        
        return state
    
    async def _extraversion_node(self, state: AgentState) -> AgentState:
        """외향성 에이전트 노드"""
        if self.callback:
            self.callback("🌟 외향성 에이전트가 분석 중...")
        
        response = await self.big5_agents["Extraversion"].generate_response(state)
        state["agent_responses"]["Extraversion"] = response
        state["reasoning_chain"].append({
            "agent": "Extraversion",
            "response": response
        })
        
        if self.callback:
            self.callback(f"🌟 외향성: {response[:50]}..." if len(response) > 50 else f"🌟 외향성: {response}")
        
        return state
    
    async def _agreeableness_node(self, state: AgentState) -> AgentState:
        """친화성 에이전트 노드"""
        if self.callback:
            self.callback("🤝 친화성 에이전트가 분석 중...")
        
        response = await self.big5_agents["Agreeableness"].generate_response(state)
        state["agent_responses"]["Agreeableness"] = response
        state["reasoning_chain"].append({
            "agent": "Agreeableness", 
            "response": response
        })
        
        if self.callback:
            self.callback(f"🤝 친화성: {response[:50]}..." if len(response) > 50 else f"🤝 친화성: {response}")
        
        return state
    
    async def _conscientiousness_node(self, state: AgentState) -> AgentState:
        """성실성 에이전트 노드"""
        if self.callback:
            self.callback("📋 성실성 에이전트가 분석 중...")
        
        response = await self.big5_agents["Conscientiousness"].generate_response(state)
        state["agent_responses"]["Conscientiousness"] = response
        state["reasoning_chain"].append({
            "agent": "Conscientiousness",
            "response": response
        })
        
        if self.callback:
            self.callback(f"📋 성실성: {response[:50]}..." if len(response) > 50 else f"📋 성실성: {response}")
        
        return state
    
    async def _neuroticism_node(self, state: AgentState) -> AgentState:
        """신경성 에이전트 노드"""
        if self.callback:
            self.callback("🤔 신경성 에이전트가 분석 중...")
        
        response = await self.big5_agents["Neuroticism"].generate_response(state)
        state["agent_responses"]["Neuroticism"] = response
        state["reasoning_chain"].append({
            "agent": "Neuroticism",
            "response": response
        })
        
        if self.callback:
            self.callback(f"🤔 신경성: {response[:50]}..." if len(response) > 50 else f"🤔 신경성: {response}")
        
        return state
    
    async def _openness_node(self, state: AgentState) -> AgentState:
        """개방성 에이전트 노드"""
        if self.callback:
            self.callback("🎨 개방성 에이전트가 분석 중...")
        
        response = await self.big5_agents["Openness"].generate_response(state)
        state["agent_responses"]["Openness"] = response
        state["reasoning_chain"].append({
            "agent": "Openness",
            "response": response
        })
        
        if self.callback:
            self.callback(f"🎨 개방성: {response[:50]}..." if len(response) > 50 else f"🎨 개방성: {response}")
        
        return state
    
    async def _synthesis_node(self, state: AgentState) -> AgentState:
        """최종 종합 노드"""
        if self.callback:
            self.callback("🎯 모든 에이전트 의견을 종합 중...")
        
        all_responses = "\n\n".join([
            f"**{agent}**: {response}"
            for agent, response in state["agent_responses"].items()
        ])
        
        synthesis_prompt = f"""
        5명의 Big5 성격 에이전트들이 각자의 관점에서 조언을 제공했습니다.
        이들의 의견을 종합하여 2-3문장으로 핵심 메시지를 정리해주세요.
        
        에이전트 응답들:
        {all_responses}
        
        사용자에게 가장 도움이 될 핵심 조언을 간결하게 정리하세요.
        """
        
        try:
            synthesis = await self.gms_client.chat_completion_async(
                messages=[
                    {"role": "system", "content": "당신은 여러 관점을 종합하여 핵심을 정리하는 전문가입니다."},
                    {"role": "user", "content": synthesis_prompt}
                ],
                max_completion_tokens=200
            )
            state["final_conclusion"] = synthesis.strip()
            if self.callback:
                self.callback("✅ 종합 분석 완료!")
        except Exception as e:
            logger.error(f"종합 응답 API 호출 실패: {e}")
            # API 실패 시 기본 응답 제공
            state["final_conclusion"] = "API 호출 실패로 종합 분석을 제공할 수 없습니다. 각 에이전트의 개별 응답을 참고해주세요."
            if self.callback:
                self.callback("⚠️ 종합 분석 중 오류 발생")
        
        return state
    
    async def process_conversation(self, user_input: str, big5_scores: Dict[str, float]) -> AgentState:
        """전체 대화 처리 프로세스"""
        initial_state = AgentState(
            messages=[],
            user_input=user_input,
            big5_scores=big5_scores,
            domain_classification="",
            agent_responses={},
            reasoning_chain=[],
            final_conclusion=""
        )
        
        try:
            final_state = await self.graph.ainvoke(initial_state)
            
            # API 실패가 많은 경우 감지
            failed_responses = sum(1 for response in final_state["agent_responses"].values() 
                                 if "API 호출에 실패" in response)
            
            if failed_responses >= 3:
                logger.warning(f"API 호출 실패가 {failed_responses}개 발생했습니다. 시스템을 중단합니다.")
                final_state["final_conclusion"] = "API 호출 실패가 다수 발생하여 정상적인 응답을 제공할 수 없습니다. 나중에 다시 시도해주세요."
            
            return final_state
        except Exception as e:
            logger.error(f"대화 처리 중 치명적 오류: {e}")
            # 치명적 오류 시 기본 응답으로 복구
            error_state = initial_state.copy()
            error_state["final_conclusion"] = "시스템 오류가 발생했습니다. 나중에 다시 시도해주세요."
            error_state["domain_classification"] = "ERROR"
            return error_state

# 사용 예시
async def main():
    system = ReasoningPersonaSystem()
    
    # 외부에서 받아온 Big5 점수 (예시)
    big5_scores = {
        "Extraversion": 0.8,
        "Agreeableness": 0.6, 
        "Conscientiousness": 0.9,
        "Neuroticism": 0.3,
        "Openness": 0.7
    }
    
    user_input = "새로운 직장에 적응하는 것이 어려워요. 동료들과 어떻게 친해져야 할지 모르겠어요."
    
    result = await system.process_conversation(user_input, big5_scores)
    
    print("=== 추론 과정 ===")
    for step in result["reasoning_chain"]:
        if "action" in step:
            print(f"{step['agent']}: {step['action']}")
        else:
            print(f"{step['agent']}: {step['response']}")
        print()
    
    print("=== 최종 종합 ===")
    print(result["final_conclusion"])

if __name__ == "__main__":
    asyncio.run(main())