package com.myocean.domain.user.listener;

import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonaActorCreator implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @PostPersist
    public void createPersonaActor(UserPersona persona) {
        log.info("UserPersona 생성 후 Actor 자동 생성 - personaId: {}", persona.getId());

        ActorRepository actorRepository = context.getBean(ActorRepository.class);

        Actor personaActor = Actor.builder()
                .kind(ActorKind.PERSONA)
                .persona(persona)
                .build();

        actorRepository.save(personaActor);
        log.info("PERSONA 타입 Actor 생성 완료 - personaId: {}, actorId: {}", persona.getId(), personaActor.getId());
    }
}
