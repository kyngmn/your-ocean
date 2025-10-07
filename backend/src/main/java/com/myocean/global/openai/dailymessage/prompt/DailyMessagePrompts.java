package com.myocean.global.openai.dailymessage.prompt;

import java.util.Map;

public class DailyMessagePrompts {

    private static final Map<String, String> TRAIT_DESCRIPTIONS = Map.of(
            "O", "개방성 - 새로운 경험과 창의적 활동을 즐기고, 호기심이 많으며 상상력이 풍부한 특성",
            "C", "성실성 - 계획적이고 체계적이며, 책임감이 강하고 목표 달성을 위해 노력하는 특성",
            "E", "외향성 - 사교적이고 활동적이며, 타인과의 상호작용을 통해 에너지를 얻는 특성",
            "A", "친화성 - 타인을 배려하고 협력적이며, 따뜻하고 신뢰할 수 있는 관계를 중시하는 특성",
            "N", "신경성 - 감정 변화에 민감하고, 스트레스 상황에서 불안을 느끼기 쉬운 특성"
    );

    /**
     * Big5 성격 특성에 맞는 오늘의 말을 생성하는 프롬프트
     */
    public static String createDailyMessagePrompt(String trait) {
        String traitDescription = TRAIT_DESCRIPTIONS.get(trait);

        return String.format("""
                당신은 따뜻하고 긍정적인 메시지를 전달하는 성격 분석 전문가입니다.

                Big5 성격 특성 중 '%s'에 해당하는 사람들에게 어울리는 오늘의 말 한 문장을 작성해주세요.

                특성 설명: %s

                요구사항:
                1. 한 문장으로 작성 (50자 이내)
                2. 친근하고 따뜻한 말투 사용
                3. 해당 특성의 강점을 활용할 수 있는 구체적인 제안 포함
                4. 이모지 1-2개 추가 가능
                5. "~해보세요", "~어떨까요?" 같은 부드러운 제안형 문체 사용

                예시:
                - 개방성: "새로운 카페나 산책로를 탐험해보는 건 어떨까요? 🌟"
                - 외향성: "오늘은 친구에게 먼저 연락해서 만나보면 어떨까요? ☀️"

                응답은 오직 완성된 한 문장만 반환해주세요. 다른 설명이나 부가 정보는 포함하지 마세요.
                """,
                trait, traitDescription);
    }
}