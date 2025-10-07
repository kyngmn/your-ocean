package com.myocean.global.openai.chatanalysis.prompt;

import com.myocean.global.openai.common.persona.PersonaCharacteristics;

public class ChatAnalysisPrompts {

    public static String createChatAnalysisPrompt(String message, String originalAnalysis) {
        return String.format(
            """
            사용자의 채팅 메시지를 분석해서 OCEAN 성격 모델 점수를 계산하고,
            상위 3개 성격이 사용자에게 친구처럼 대화를 거는 메시지를 생성해주세요.

            ## 사용자 채팅 메시지
            "%s"

            ## 요청사항
            1. Big5 점수 재계산: 사용자 메시지를 분석해서 0.0~1.0 사이로 각 성격 점수를 계산해주세요.
               - AI 서버의 점수(모두 0.5)는 무시하고 메시지 내용을 직접 분석하세요.

            2. 상위 3개 선별: 계산된 점수 중 가장 높은 3개 성격을 선택합니다.

            3. 친구같은 대화 생성: 선별된 3개 성격이 친구처럼 편안하고 자연스럽게 대화합니다.

            %s

            ## 각 성격별 분석 기준:
            - Openness: 창의성, 호기심, 새로운 경험에 대한 개방성
            - Conscientiousness: 책임감, 계획성, 체계성, 신중함
            - Extraversion: 사교성, 활발함, 에너지, 외향적 성향
            - Agreeableness: 협조성, 친화성, 타인에 대한 배려
            - Neuroticism: 감정적 불안정성, 스트레스, 걱정, 부정적 감정

            ## 예시:
            사용자: "오늘 정말 짜증이 많이 나서 하루종일 집에 있었어"

            분석 결과:
            - Neuroticism: 0.9 (부정적 감정, 스트레스 표현)
            - Extraversion: 0.3 (집에만 있음, 외향적 행동 부족)
            - Openness: 0.7 (새로운 상황에 대한 반응)

            응답 (친구들끼리 대화하듯이):
            - Neuroticism: "아 정말 힘든 하루였구나ㅠㅠ 나도 그런 적 있어... 괜찮아?"
            - Openness: "오? 뭔가 새로운 일이 있었나? 어떤 상황이었어?"
            - Extraversion: "어? 왜 그랬어?? 무슨 일 있었어? 나한테 얘기해봐!"

            다음 JSON 형식으로 응답해주세요:
            {
              "big5_scores": {
                "openness": 0.7,
                "conscientiousness": 0.4,
                "extraversion": 0.3,
                "agreeableness": 0.6,
                "neuroticism": 0.9
              },
              "domain_classification": "NEUROTICISM",
              "agent_responses": {
                "Neuroticism": "아 정말 힘든 하루였구나ㅠㅠ 나도 그런 적 있어... 괜찮아?",
                "Openness": "오? 뭔가 새로운 일이 있었나? 어떤 상황이었어?",
                "Extraversion": "어? 왜 그랬어?? 무슨 일 있었어? 나한테 얘기해봐!"
              }
            }

            주의사항:
            - Big5 점수는 메시지 내용을 실제로 분석해서 계산하세요 (0.5 고정값 사용 금지)
            - agent_responses에는 점수가 높은 상위 3개 성격만 포함하세요
            - 각 메시지는 친구끼리 대화하듯이 편안하고 자연스럽게 작성하세요
            - 반말과 이모티콘('ㅠㅠ', '!', '~', '??')을 적극 사용해서 친근함을 표현하세요
            - JSON 형식만 응답하고 다른 텍스트는 포함하지 마세요

            """,
            message, PersonaCharacteristics.getPersonaConversationStyle()
        );
    }
}