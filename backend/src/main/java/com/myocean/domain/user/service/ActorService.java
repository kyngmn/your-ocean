package com.myocean.domain.user.service;

import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import com.myocean.domain.user.repository.UserPersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActorService {

    private final ActorRepository actorRepository;
    private final UserPersonaRepository userPersonaRepository;

    /**
     * 새로운 사용자를 위한 기본 액터들을 생성합니다.
     * - 5개의 기본 UserPersona 생성
     * - 5개의 PERSONA 액터 생성
     * - 1개의 USER 액터 생성
     */
    public void createDefaultActorsForUser(Integer userId) {
        log.info("👥 사용자 {}의 기본 액터 생성 시작", userId);

        try {
            // 1. 기본 5개의 UserPersona 생성
            List<UserPersona> defaultPersonas = createDefaultPersonas(userId);
            log.info("👥 기본 페르소나 5개 생성 완료 - 사용자 ID: {}", userId);

            // 2. 각 페르소나에 대한 PERSONA 액터 생성
            List<Actor> actors = new ArrayList<>();
            for (UserPersona persona : defaultPersonas) {
                Actor personaActor = Actor.builder()
                        .kind(ActorKind.PERSONA)
                        .userId(userId)
                        .personaId(persona.getId())
                        .build();
                actors.add(personaActor);
            }

            // 3. USER 액터 생성
            Actor userActor = Actor.builder()
                    .kind(ActorKind.USER)
                    .userId(userId)
                    .personaId(null)
                    .build();
            actors.add(userActor);

            // 4. 모든 액터 저장
            List<Actor> savedActors = actorRepository.saveAll(actors);
            log.info("👥 기본 액터 {}개 생성 완료 - 사용자 ID: {}", savedActors.size(), userId);

        } catch (Exception e) {
            log.error("❌ 사용자 {}의 기본 액터 생성 실패: {}", userId, e.getMessage(), e);
            throw new RuntimeException("기본 액터 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 기본 5개의 UserPersona를 생성합니다.
     * Big5 성격 모델을 기반으로 한 다양한 페르소나들을 생성합니다.
     */
    private List<UserPersona> createDefaultPersonas(Integer userId) {
        List<UserPersona> personas = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 1. 개방성 중심 페르소나 (O=100, 나머지=50)
        personas.add(UserPersona.builder()
                .userId(userId)
                .userA(50)  // Agreeableness
                .userC(50)  // Conscientiousness
                .userE(50)  // Extraversion
                .userN(50)  // Neuroticism
                .userO(100) // Openness
                .build());

        // 2. 성실성 중심 페르소나 (C=100, 나머지=50)
        personas.add(UserPersona.builder()
                .userId(userId)
                .userA(50)
                .userC(100)
                .userE(50)
                .userN(50)
                .userO(50)
                .build());

        // 3. 외향성 중심 페르소나 (E=100, 나머지=50)
        personas.add(UserPersona.builder()
                .userId(userId)
                .userA(50)
                .userC(50)
                .userE(100)
                .userN(50)
                .userO(50)
                .build());

        // 4. 친화성 중심 페르소나 (A=100, 나머지=50)
        personas.add(UserPersona.builder()
                .userId(userId)
                .userA(100)
                .userC(50)
                .userE(50)
                .userN(50)
                .userO(50)
                .build());

        // 5. 신경성 중심 페르소나 (N=100, 나머지=50)
        personas.add(UserPersona.builder()
                .userId(userId)
                .userA(50)
                .userC(50)
                .userE(50)
                .userN(100)
                .userO(50)
                .build());

        return userPersonaRepository.saveAll(personas);
    }

    /**
     * 사용자가 기본 액터들을 가지고 있는지 확인합니다.
     */
    @Transactional(readOnly = true)
    public boolean hasDefaultActors(Integer userId) {
        List<Actor> userActors = actorRepository.findByUserId(userId);
        return userActors.size() >= 6; // 5개 PERSONA + 1개 USER
    }
}