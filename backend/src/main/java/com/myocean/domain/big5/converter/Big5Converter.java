package com.myocean.domain.big5.converter;

import com.myocean.domain.big5.dto.request.Big5ResultCreateRequest;
import com.myocean.domain.big5.dto.request.Big5ResultUpdateRequest;
import com.myocean.domain.big5.dto.response.Big5ResultResponse;
import com.myocean.domain.big5.dto.response.Big5ScoresResponse;
import com.myocean.domain.big5.entity.Big5Result;
import org.springframework.stereotype.Component;

@Component
public class Big5Converter {

    public Big5Result toEntity(Integer userId, Big5ResultCreateRequest request) {
        return Big5Result.builder()
                .userId(userId)
                .sourceType(request.getSourceType())
                .sourceId(request.getSourceId())
                .resultO(request.getOpenness())
                .resultC(request.getConscientiousness())
                .resultE(request.getExtraversion())
                .resultA(request.getAgreeableness())
                .resultN(request.getNeuroticism())
                .build();
    }

    public Big5Result toEntity(Big5Result existing, Big5ResultUpdateRequest request) {
        return Big5Result.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .sourceType(existing.getSourceType())
                .sourceId(existing.getSourceId())
                .resultO(request.getOpenness())
                .resultC(request.getConscientiousness())
                .resultE(request.getExtraversion())
                .resultA(request.getAgreeableness())
                .resultN(request.getNeuroticism())
                .computedAt(existing.getComputedAt())
                .build();
    }

    public Big5ResultResponse toResponse(Big5Result entity) {
        Big5ResultResponse build = Big5ResultResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .sourceType(entity.getSourceType())
                .sourceId(entity.getSourceId())
                .resultO(entity.getResultO() != null ? entity.getResultO() : null)
                .resultC(entity.getResultC() != null ? entity.getResultC() : null)
                .resultE(entity.getResultE() != null ? entity.getResultE() : null)
                .resultA(entity.getResultA() != null ? entity.getResultA() : null)
                .resultN(entity.getResultN() != null ? entity.getResultN() : null)
                .computedAt(entity.getComputedAt())
                .build();
        return build;
    }

    public Big5ScoresResponse toScoresResponse(Big5Result entity) {
        return Big5ScoresResponse.builder()
                .openness(entity.getResultO() != null ? entity.getResultO() : null)
                .conscientiousness(entity.getResultC() != null ? entity.getResultC(): null)
                .extraversion(entity.getResultE() != null ? entity.getResultE() : null)
                .agreeableness(entity.getResultA() != null ? entity.getResultA() : null)
                .neuroticism(entity.getResultN() != null ? entity.getResultN() : null)
                .build();
    }
}