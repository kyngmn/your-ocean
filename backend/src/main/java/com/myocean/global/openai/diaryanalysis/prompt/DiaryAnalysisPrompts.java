package com.myocean.global.openai.diaryanalysis.prompt;

import com.myocean.global.openai.common.persona.PersonaCharacteristics;

public class DiaryAnalysisPrompts {

    public static String createDiaryAnalysisPrompt(String title, String content, String originalAnalysis) {
        return String.format(
            """
            다음은 사용자의 다이어리 내용과 AI 서버에서 분석한 결과입니다.
            AI 서버의 분석 결과를 바탕으로 OCEAN 성격 모델의 각 요소에 대해 더 자연스럽고 따뜻한 톤으로 메시지를 다듬어주세요.

            ## 다이어리 정보
            제목: %s
            내용: %s

            ## AI 서버 분석 결과
            %s

            ## 요청사항
            다이어리 내용을 분석하여 다음을 수행해주세요:

            1. 다이어리 내용을 바탕으로 OCEAN 모델의 Big5 점수를 0.0~1.0 사이로 계산해주세요.
               - Openness: 창의성, 호기심, 새로운 경험에 대한 개방성
               - Conscientiousness: 책임감, 계획성, 체계성, 신중함
               - Extraversion: 사교성, 활발함, 에너지, 외향적 성향
               - Agreeableness: 협조성, 친화성, 타인에 대한 배려
               - Neuroticism: 감정적 불안정성, 스트레스, 걱정, 부정적 감정

            2. OCEAN 모델의 5가지 성격 페르소나가 친구들처럼 편안하게 대화하며 사용자의 다이어리에 대해 이야기하는 시나리오를 만들어주세요.
               - 각 페르소나는 자신의 성격 특성에 맞게 다이어리 내용을 해석하고 친구처럼 조언합니다
               - 서로의 말에 "맞아!", "그런데", "아니야" 등으로 자연스럽게 반응하며 대화를 이어갑니다
               - 친구들끼리 단톡방에서 대화하듯이 편안하고 친근한 톤으로 구성해주세요

            %s

            2. 각 페르소나의 특성:
               - Openness (개방성): 창의적이고 호기심 많으며, 새로운 경험과 아이디어에 열린 성격
               - Conscientiousness (성실성): 체계적이고 책임감 있으며, 계획적이고 신중한 성격
               - Extraversion (외향성): 활발하고 사교적이며, 에너지 넘치고 적극적인 성격
               - Agreeableness (친화성): 따뜻하고 배려깊으며, 협력적이고 이해심 많은 성격
               - Neuroticism (신경성): 섬세하고 감정적이며, 걱정과 불안을 솔직하게 표현하는 성격

            3. 이 다이어리를 대변하는 성격이나 감정을 나타내는 키워드 3개를 추출해주세요.

            다음 JSON 형식으로 응답해주세요:
            {
              "agent_responses": {
                "Openness": "개방성 페르소나가 친구처럼 대화에서 하는 말 (반말, 이모티콘 사용하며 자연스럽게)",
                "Conscientiousness": "성실성 페르소나가 친구처럼 대화에서 하는 말 (반말, 이모티콘 사용하며 자연스럽게)",
                "Extraversion": "외향성 페르소나가 친구처럼 대화에서 하는 말 (반말, 이모티콘 사용하며 자연스럽게)",
                "Agreeableness": "친화성 페르소나가 친구처럼 대화에서 하는 말 (반말, 이모티콘 사용하며 자연스럽게)",
                "Neuroticism": "신경성 페르소나가 친구처럼 대화에서 하는 말 (반말, 이모티콘 사용하며 자연스럽게)"
              },
              "keywords": ["키워드1", "키워드2", "키워드3"],
              "big5_scores": {
                "openness": [다이어리 내용 분석 결과에 따른 실제 계산 점수],
                "conscientiousness": [다이어리 내용 분석 결과에 따른 실제 계산 점수],
                "extraversion": [다이어리 내용 분석 결과에 따른 실제 계산 점수],
                "agreeableness": [다이어리 내용 분석 결과에 따른 실제 계산 점수],
                "neuroticism": [다이어리 내용 분석 결과에 따른 실제 계산 점수]
              },
              "domain_classification": "분석 결과에 따른 주요 성격 분류",
              "final_conclusion": "다이어리 분석을 바탕으로 작성한 최종 결론"
            }

            JSON 형식만 응답하고 다른 텍스트는 포함하지 마세요.
            """,
            title, content, originalAnalysis, PersonaCharacteristics.getPersonaConversationStyle()
        );
    }
}