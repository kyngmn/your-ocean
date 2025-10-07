package com.myocean.domain.user.converter;

import com.myocean.domain.user.dto.response.UserPersonaResponse;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.global.enums.BigCode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserPersonaConverter {

    public UserPersonaResponse toResponse(List<UserPersona> personas) {
        if (personas.isEmpty()) {
            return null;
        }

        UserPersona firstPersona = personas.get(0);
        Integer userId = firstPersona.getUserId();
        Integer id = firstPersona.getId();

        // BigCode별 점수 맵 생성
        Map<BigCode, Short> scores = personas.stream()
                .collect(Collectors.toMap(
                    UserPersona::getBigCode,
                    UserPersona::getScore
                ));

        // 가장 최근 생성/수정 시간 찾기
        LocalDateTime latestCreatedAt = personas.stream()
                .map(UserPersona::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestUpdatedAt = personas.stream()
                .map(UserPersona::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new UserPersonaResponse(
                id,
                userId,
                scores.get(BigCode.O),
                scores.get(BigCode.C),
                scores.get(BigCode.E),
                scores.get(BigCode.A),
                scores.get(BigCode.N),
                latestCreatedAt,
                latestUpdatedAt
        );
    }
}

/*
  {
    "id": 1,
    "userId": 10,
    "userO": 75,
    "userC": 82,
    "userE": 60,
    "userA": 90,
    "userN": 45,
    "createdAt": "2025-10-08T10:30:00",
    "updatedAt": "2025-10-08T15:45:00"
  }
 */