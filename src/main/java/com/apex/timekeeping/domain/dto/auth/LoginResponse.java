package com.apex.timekeeping.domain.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private Long userId;
        private Long accountId;
        private String username;
        private String fullName;
        private String role;
        private String email;
    }
}
