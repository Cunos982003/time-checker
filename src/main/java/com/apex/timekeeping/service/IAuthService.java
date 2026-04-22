package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.auth.LoginRequest;
import com.apex.timekeeping.domain.dto.auth.LoginResponse;
import com.apex.timekeeping.domain.dto.auth.RefreshTokenRequest;

public interface IAuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
    void logout(Long accountId);
}
