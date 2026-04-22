package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.domain.dto.auth.LoginRequest;
import com.apex.timekeeping.domain.dto.auth.LoginResponse;
import com.apex.timekeeping.domain.dto.auth.RefreshTokenRequest;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.service.IAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(req)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refreshToken(req)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails user) {
        authService.logout(user.getAccountId());
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> me(@AuthenticationPrincipal CustomUserDetails user) {
        LoginResponse.UserInfo info = LoginResponse.UserInfo.builder()
                .userId(user.getUserId())
                .accountId(user.getAccountId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(info));
    }
}
