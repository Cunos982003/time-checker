package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.common.PagedResponse;
import com.apex.timekeeping.domain.dto.attendance.ApprovalRequest;
import com.apex.timekeeping.domain.dto.attendance.AttendanceResponse;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationRequest;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationResponse;
import com.apex.timekeeping.security.CustomUserDetails;
import com.apex.timekeeping.service.IAttendanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance")
public class AttendanceController {

    private final IAttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.checkIn(user.getUserId())));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.checkOut(user.getUserId())));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<AttendanceResponse>> today(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.getTodayStatus(user.getUserId())));
    }

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> monthly(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int month) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.getMonthlyAttendance(user.getUserId(), year, month)));
    }

    @GetMapping("/monthly/{userId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> monthlyForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        int m = month == 0 ? LocalDate.now().getMonthValue() : month;
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.getMonthlyAttendance(userId, y, m)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AttendanceResponse>>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(attendanceService.getAllAttendance(date, pageable))));
    }

    // ----- Time Explanations -----
    @PostMapping("/explanations")
    public ResponseEntity<ApiResponse<TimeExplanationResponse>> createExplanation(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody TimeExplanationRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.createExplanation(user.getUserId(), req)));
    }

    @GetMapping("/explanations")
    public ResponseEntity<ApiResponse<List<TimeExplanationResponse>>> myExplanations(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.getMyExplanations(user.getUserId())));
    }

    @GetMapping("/explanations/pending")
    public ResponseEntity<ApiResponse<PagedResponse<TimeExplanationResponse>>> pendingExplanations(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(attendanceService.getPendingExplanations(user.getUserId(), pageable))));
    }

    @PostMapping("/explanations/{id}/approve")
    public ResponseEntity<ApiResponse<TimeExplanationResponse>> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ApprovalRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.approveExplanation(id, user.getUserId(), req.getNote())));
    }

    @PostMapping("/explanations/{id}/reject")
    public ResponseEntity<ApiResponse<TimeExplanationResponse>> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ApprovalRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.rejectExplanation(id, user.getUserId(), req.getNote())));
    }
}
