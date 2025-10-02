package com.myocean.domain.user.dto.converter;

import com.myocean.domain.user.dto.response.UserPersonaResponse;
import com.myocean.domain.user.entity.UserPersona;
import org.springframework.stereotype.Component;

@Component
public class UserPersonaConverter {

    public UserPersonaResponse toResponse(UserPersona persona) {
        return new UserPersonaResponse(
                persona.getId(),
                persona.getUserId(),
                persona.getUserO(),
                persona.getUserC(),
                persona.getUserE(),
                persona.getUserA(),
                persona.getUserN(),
                persona.getCreatedAt(),
                persona.getUpdatedAt()
        );
    }
}