package com.myocean.domain.user.service;

import com.myocean.domain.user.dto.converter.UserPersonaConverter;
import com.myocean.domain.user.dto.response.UserPersonaResponse;
import com.myocean.domain.user.entity.UserPersona;
import com.myocean.domain.user.repository.UserPersonaRepository;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
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
    public UserPersonaResponse getUserPersona(Integer userId) {
        // 유저 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        List<UserPersona> personas = userPersonaRepository.findByUserIdAndDeletedAtIsNull(userId);

        if (personas.isEmpty()) {return null;}

        // 첫 번째 페르소나 반환 (일반적으로 유저당 하나의 페르소나)
        return userPersonaConverter.toResponse(personas.get(0));
    }
}