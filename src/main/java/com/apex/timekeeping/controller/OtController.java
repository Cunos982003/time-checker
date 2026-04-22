package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.common.PagedResponse;
import com.apex.timekeeping.domain.dto.ot.OtRequest;
import com.apex.timekeeping.domain.dto.ot.OtResponse;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.service.IOtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ot")
@RequiredArgsConstructor
@Tag(name = "Overtime")
public class OtController {

    private final IOtService otService;

    @PostMapping
    public ResponseEntity<ApiResponse<OtResponse>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody OtRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(otService.create(user.getUserId(), req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OtResponse>>> myOt(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(otService.getMyOt(user.getUserId())));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<PagedResponse<OtResponse>>> pending(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(otService.getPendingForManager(user.getUserId(), PageRequest.of(page, size)))));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<OtResponse>> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(otService.approve(id, user.getUserId())));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<OtResponse>> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(otService.reject(id, user.getUserId())));
    }

    @PostMapping("/{id}/convert-to-leave")
    public ResponseEntity<ApiResponse<Void>> convertToLeave(@PathVariable Long id) {
        otService.convertToLeave(id);
        return ResponseEntity.ok(ApiResponse.ok("Converted to compensatory leave", null));
    }
}
