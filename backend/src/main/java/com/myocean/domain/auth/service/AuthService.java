package com.myocean.domain.auth.service;

import com.myocean.domain.auth.dto.request.CallbackRequest;
import com.myocean.domain.auth.dto.request.JoinRequest;
import com.myocean.domain.auth.dto.request.ReissueRequest;
import com.myocean.domain.auth.dto.response.LoginUrlResponse;
import com.myocean.domain.auth.dto.response.TokenResponse;

public interface AuthService {

    LoginUrlResponse getGoogleLoginUrl();

    TokenResponse processGoogleCallback(CallbackRequest request);

    TokenResponse processGoogleJoin(JoinRequest request);

    TokenResponse processGoogleOAuth(CallbackRequest request);

    TokenResponse reissueToken(ReissueRequest request);

    void logout(String token);
}