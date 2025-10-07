package com.myocean.domain.user.service;

import com.myocean.domain.user.converter.UserPersonaConverter;
import com.myocean.domain.user.dto.response.UserPersonaResponse;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.repository.UserPersonaRepository;
import com.myocean.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserPersonaService {

    private final UserPersonaRepository userPersonaRepository;
    private final UserRepository userRepository;
    private final UserPersonaConverter userPersonaConverter;

    @Transactional(readOnly = true)
    public UserPersonaResponse getUserPersonas(Integer userId) {
        List<UserPersona> personas = userPersonaRepository.findByUserIdAndDeletedAtIsNull(userId);

        if (personas.isEmpty()) {
            log.info("페르소나 조회 완료 - userId: {}, 페르소나 없음", userId);
            return null;
        }

        log.info("페르소나 조회 완료 - userId: {}", userId);
        return userPersonaConverter.toResponse(personas);
    }

}