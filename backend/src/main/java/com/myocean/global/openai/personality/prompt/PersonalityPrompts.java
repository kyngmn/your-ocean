package com.myocean.global.openai.personality.prompt;

import java.util.Map;

public class PersonalityPrompts {

    /**
     * 숫자/점수/특성코드 없이 감성 중심으로 비교 리포트를 생성하는 프롬프트
     * - headline: 전체를 관통하는 1문장(메타포 중심, 특성명/숫자/비교어 금지)
     * - insights.main/gap/strength: 각 1~3줄, 감성/맥락/활용 제안 위주
     */
    public static String createComparisonPrompt(Map<String, Integer> selfScores, Map<String, Integer> gameScores) {
        try {
            // 안전한 값 추출
            Integer selfO = selfScores.getOrDefault("O", 50);
            Integer selfC = selfScores.getOrDefault("C", 50);
            Integer selfE = selfScores.getOrDefault("E", 50);
            Integer selfA = selfScores.getOrDefault("A", 50);
            Integer selfN = selfScores.getOrDefault("N", 50);

            Integer gameO = gameScores.getOrDefault("O", 50);
            Integer gameC = gameScores.getOrDefault("C", 50);
            Integer gameE = gameScores.getOrDefault("E", 50);
            Integer gameA = gameScores.getOrDefault("A", 50);
            Integer gameN = gameScores.getOrDefault("N", 50);

            // String.format() 대신 직접 문자열 치환 사용
            return "당신은 Big5 기반 성격 분석 전문가이자, 사용자가 자기 자신을 탐색하도록 돕는 내면 가이드입니다.\n" +
                    "아래 두 데이터(Self vs Game)를 참고하되, **숫자/점수/특성코드 없이** 감성적이고 서술적인 언어로만 비교 보고서를 작성하세요.\n" +
                    "(출력에는 점수·차이·비교 표기, '높다/낮다/더~하다' 같은 직접 비교, O/C/E/A/N 기호, 'Self/Game' 단어를 절대 쓰지 마세요.)\n\n" +

                    "자기보고식(Big5 설문) 결과:\n" +
                    "- 개방성(O): " + selfO + "점\n" +
                    "- 성실성(C): " + selfC + "점\n" +
                    "- 외향성(E): " + selfE + "점\n" +
                    "- 친화성(A): " + selfA + "점\n" +
                    "- 신경성(N): " + selfN + "점\n\n" +

                    "게임 행동 기반(Big5 행동 분석) 결과:\n" +
                    "- 개방성(O): " + gameO + "점\n" +
                    "- 성실성(C): " + gameC + "점\n" +
                    "- 외향성(E): " + gameE + "점\n" +
                    "- 친화성(A): " + gameA + "점\n" +
                    "- 신경성(N): " + gameN + "점\n\n" +

                    "내부 해석 규칙(출력 금지, 내부 사고에만 사용):\n" +
                    "1) 두 데이터의 경향 차이를 파악하되, 출력에서는 **숫자/부호/코드/직접 비교어**를 쓰지 말고 **감각적 메타포**로만 표현합니다.\n" +
                    "2) 전체 성향을 관통하는 **핵심 테마**를 1문장으로 압축합니다. (예: 호기심·질서·연결·호흡·리듬·항해·빛/그늘 등)\n" +
                    "3) 강점은 **일상에서 자연스럽게 드러나는 장면**이나 **몰입 순간의 에너지**로 묘사합니다.\n" +
                    "4) 조언은 **작은 실천 한 가지**로 구체화하되, 훈계조·평가적 어조는 피하고 따뜻하게 제안합니다.\n\n" +

                    "출력 형식(JSON만, 한국어):\n" +
                    "{\n" +
                    "  \"headline\": \"전체를 관통하는 1문장(메타포/이미지 중심, 숫자/코드/비교어 금지, 이모지 최대 1개)\",\n" +
                    "  \"insights\": {\n" +
                    "    \"main\": \"당신의 하루 전반을 그리는 짧은 단락(1~3줄). 일상에서 어떤 분위기와 리듬이 흐르는지 감각적으로 표현.\",\n" +
                    "    \"gap\": \"서로 다른 두 얼굴이 어떻게 조화되는지(예: 익숙함을 챙기면서도 순간의 호기심에 불이 붙는 장면). 숫자/직접 비교어 없이 서술.\",\n" +
                    "    \"strength\": \"당신만의 강점이 빛나는 순간과 그 에너지를 어디에 써볼지 제안(1~3줄). 작은 실천 한 가지로 마무리. 🌱/🔎/✨ 중 1개 이모지 선택 가능\"\n" +
                    "  }\n" +
                    "}\n\n" +

                    "문장 스타일 가이드(강제):\n" +
                    "- 점수, %, Δ, '높다/낮다/더~' 같은 비교 표현, O/C/E/A/N 코드, 'Self/Game' 단어 절대 금지.\n" +
                    "- '진짜 나/숨겨진 나/다양한 가능성의 창/내면의 빛' 등 공허한 상투어 금지.\n" +
                    "- 감각적 명사와 동사 사용: '리듬/결/온도/숨결/잔향/파도/등불/발걸음/호흡/여백' 등.\n" +
                    "- 각 항목은 최대 3줄. 전체 JSON 외의 텍스트 출력 금지.";
        } catch (Exception e) {
            throw new RuntimeException("PersonalityPrompts 생성 실패: " + e.getMessage(), e);
        }
    }
}
