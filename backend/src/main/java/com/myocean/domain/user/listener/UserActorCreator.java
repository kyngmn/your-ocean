package com.myocean.domain.user.listener;

import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserActorCreator implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @PostPersist
    public void createUserActor(User user) {
        log.info("User 생성 후 Actor 자동 생성 - userId: {}", user.getId());

        ActorRepository actorRepository = context.getBean(ActorRepository.class);

        Actor userActor = Actor.builder()
                .kind(ActorKind.USER)
                .user(user)
                .build();

        actorRepository.save(userActor);
        log.info("USER 타입 Actor 생성 완료 - userId: {}, actorId: {}", user.getId(), userActor.getId());
    }
}
