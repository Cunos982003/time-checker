package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.auth.LoginRequest;
import com.apex.timekeeping.domain.dto.auth.LoginResponse;
import com.apex.timekeeping.domain.dto.auth.RefreshTokenRequest;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.RefreshToken;
import com.apex.timekeeping.domain.entity.UserAccount;
import com.apex.timekeeping.domain.repository.RefreshTokenRepository;
import com.apex.timekeeping.domain.repository.UserAccountRepository;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshTokenStr = jwtUtil.generateRefreshToken(userDetails.getUsername());

        UserAccount account = userAccountRepository.findById(userDetails.getAccountId())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        // Revoke old tokens and save new one
        refreshTokenRepository.revokeAllByAccountId(account.getAccountId());
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshTokenStr);
        rt.setUserAccount(account);
        rt.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));
        refreshTokenRepository.save(rt);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(LoginResponse.UserInfo.builder()
                        .userId(userDetails.getUserId())
                        .accountId(userDetails.getAccountId())
                        .username(userDetails.getUsername())
                        .fullName(userDetails.getFullName())
                        .role(userDetails.getRole())
                        .build())
                .build();
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken rt = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new JwtException("Refresh token not found"));

        if (rt.getRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new JwtException("Refresh token expired or revoked");
        }

        UserAccount account = rt.getUserAccount();
        String username = jwtUtil.extractUsername(rt.getToken());

        // Build a minimal CustomUserDetails from account
        CustomUserDetails userDetails = buildUserDetails(account);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(rt.getToken())
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    @Transactional
    public void logout(Long accountId) {
        refreshTokenRepository.revokeAllByAccountId(accountId);
    }

    private CustomUserDetails buildUserDetails(UserAccount account) {
        Employee emp = account.getEmployee();
        return CustomUserDetails.builder()
                .accountId(account.getAccountId())
                .userId(emp.getUserId())
                .username(account.getUsername())
                .password(account.getPassword())
                .role(emp.getRole() != null ? emp.getRole().getRoleName() : "USER")
                .fullName(emp.getFullname())
                .build();
    }
}
