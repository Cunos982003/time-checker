package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.attendance.AttendanceResponse;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationRequest;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IAttendanceService {
    AttendanceResponse checkIn(Long userId);
    AttendanceResponse checkOut(Long userId);
    AttendanceResponse getTodayStatus(Long userId);
    List<AttendanceResponse> getMonthlyAttendance(Long userId, int year, int month);
    Page<AttendanceResponse> getAllAttendance(LocalDate date, Pageable pageable);
    TimeExplanationResponse createExplanation(Long userId, TimeExplanationRequest req);
    List<TimeExplanationResponse> getMyExplanations(Long userId);
    Page<TimeExplanationResponse> getPendingExplanations(Long managerId, Pageable pageable);
    TimeExplanationResponse approveExplanation(Long explainId, Long approverId, String note);
    TimeExplanationResponse rejectExplanation(Long explainId, Long approverId, String note);
}
