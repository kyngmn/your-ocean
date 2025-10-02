package com.myocean.domain.user.dto.converter;

import com.myocean.domain.user.dto.request.CreateUserRequest;
import com.myocean.domain.user.dto.response.UserResponse;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.enums.AiStatus;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .email(request.email())
                .provider(request.provider())
                .socialId(request.socialId())
                .nickname(request.nickname())
                .profileImageUrl(request.profileImageUrl())
                .aiStatus(AiStatus.UNSET)
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getProvider(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getAiStatus()
        );
    }
}