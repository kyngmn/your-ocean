package com.myocean.domain.user.converter;

import com.myocean.domain.user.dto.response.UserResponse;
import com.myocean.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

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