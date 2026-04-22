package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.common.PagedResponse;
import com.apex.timekeeping.domain.dto.notification.NotificationRequest;
import com.apex.timekeeping.domain.dto.notification.NotificationResponse;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.service.INotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(notificationService.findAll(PageRequest.of(page, size)))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody NotificationRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.create(user.getUserId(), req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }
}
