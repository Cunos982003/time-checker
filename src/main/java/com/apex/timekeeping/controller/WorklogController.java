package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.common.PagedResponse;
import com.apex.timekeeping.domain.dto.worklog.WorklogRequest;
import com.apex.timekeeping.domain.dto.worklog.WorklogResponse;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.service.WorklogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worklogs")
@RequiredArgsConstructor
@Tag(name = "Worklog")
public class WorklogController {

    private final WorklogService worklogService;

    @PostMapping
    public ResponseEntity<ApiResponse<WorklogResponse>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody WorklogRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(worklogService.create(user.getUserId(), req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorklogResponse>>> myWorklogs(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(worklogService.getMyWorklogs(user.getUserId())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorklogResponse>> update(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody WorklogRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(worklogService.update(id, user.getUserId(), req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        worklogService.delete(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<PagedResponse<WorklogResponse>>> pending(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(worklogService.getPendingForManager(user.getUserId(), PageRequest.of(page, size)))));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<WorklogResponse>> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody(required = false) String note) {
        return ResponseEntity.ok(ApiResponse.ok(worklogService.approve(id, user.getUserId(), note)));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<WorklogResponse>> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody(required = false) String note) {
        return ResponseEntity.ok(ApiResponse.ok(worklogService.reject(id, user.getUserId(), note)));
    }
}
