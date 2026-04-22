package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.common.PagedResponse;
import com.apex.timekeeping.domain.dto.leave.LeaveBalanceResponse;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestDto;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestResponse;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.service.LeaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
@Tag(name = "Leave")
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> balance(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getMyBalances(user.getUserId())));
    }

    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody LeaveRequestDto req) {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.create(user.getUserId(), req)));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponse>>> myRequests(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getMyRequests(user.getUserId())));
    }

    @PostMapping("/requests/{id}/cancel")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.cancel(id, user.getUserId())));
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<PagedResponse<LeaveRequestResponse>>> pending(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(leaveService.getPendingForManager(user.getUserId(), pageable))));
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.approve(id, user.getUserId())));
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.reject(id, user.getUserId(), reason)));
    }
}
