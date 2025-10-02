package com.myocean.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google OAuth 콜백 요청")
public record CallbackRequest(
        @Schema(description = "Google에서 받은 authorization code", example = "4/P7q7W91a-oMsCeLvIaQm6bTrgtp7")
        String code,

        @Schema(description = "CSRF 방지를 위한 state 값", example = "random_state_string")
        String state
) {
}