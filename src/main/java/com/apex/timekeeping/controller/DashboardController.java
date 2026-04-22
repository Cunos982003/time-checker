package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.domain.dto.dashboard.DashboardResponse;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.service.IDashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDashboard(user.getUserId())));
    }
}
