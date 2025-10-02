package com.myocean.global.ai.util;

import com.myocean.domain.survey.enums.BigCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Big5AgentMapper {

    private static final Map<String, BigCode> AGENT_TO_BIG_CODE = Map.of(
            "Extraversion", BigCode.E,
            "Agreeableness", BigCode.A,
            "Conscientiousness", BigCode.C,
            "Neuroticism", BigCode.N,
            "Openness", BigCode.O
    );

    /**
     * AI Agent 이름을 BigCode로 변환
     */
    public static BigCode mapAgentNameToBigCode(String agentName) {
        return AGENT_TO_BIG_CODE.get(agentName);
    }
}