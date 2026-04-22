package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.attendance.AttendanceResponse;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationRequest;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationResponse;
import com.apex.timekeeping.domain.entity.ConfirmExplanation;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.TimeEntry;
import com.apex.timekeeping.domain.entity.TimeExplanation;
import com.apex.timekeeping.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final TimeEntryRepository timeEntryRepository;
    private final WorkingDayRepository workingDayRepository;
    private final TimeExplanationRepository timeExplanationRepository;
    private final ConfirmExplanationRepository confirmExplanationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @Transactional
    public AttendanceResponse checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        if (timeEntryRepository.existsByEmployee_UserIdAndCheckinDate(userId, today)) {
            throw new BusinessException("Already checked in today");
        }

        Employee emp = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        TimeEntry entry = new TimeEntry();
        entry.setEmployee(emp);
        entry.setCheckinDate(today);
        entry.setCheckinTime(LocalTime.now());

        // Compute late minutes
        workingDayRepository.findById(today).ifPresent(wd -> {
            if (wd.getCheckinTime() != null && entry.getCheckinTime().isAfter(wd.getCheckinTime())) {
                int lateMinutes = (int) java.time.Duration.between(wd.getCheckinTime(), entry.getCheckinTime()).toMinutes();
                entry.setNumberMinutesLate(lateMinutes);
            }
        });

        TimeEntry saved = timeEntryRepository.save(entry);
        return toResponse(saved);
    }

    @Transactional
    public AttendanceResponse checkOut(Long userId) {
        LocalDate today = LocalDate.now();
        TimeEntry entry = timeEntryRepository.findByEmployee_UserIdAndCheckinDate(userId, today)
                .orElseThrow(() -> new BusinessException("No check-in record found for today"));

        entry.setCheckoutDate(today);
        entry.setCheckoutTime(LocalTime.now());

        workingDayRepository.findById(today).ifPresent(wd -> {
            if (wd.getCheckoutTime() != null && entry.getCheckoutTime().isBefore(wd.getCheckoutTime())) {
                int earlyMinutes = (int) java.time.Duration.between(entry.getCheckoutTime(), wd.getCheckoutTime()).toMinutes();
                entry.setNumberMinutesQuitEarly(earlyMinutes);
            }
        });

        return toResponse(timeEntryRepository.save(entry));
    }

    public AttendanceResponse getTodayStatus(Long userId) {
        LocalDate today = LocalDate.now();
        return timeEntryRepository.findByEmployee_UserIdAndCheckinDate(userId, today)
                .map(this::toResponse)
                .orElse(AttendanceResponse.builder().checkinDate(today).status("ABSENT").build());
    }

    public List<AttendanceResponse> getMonthlyAttendance(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return timeEntryRepository.findMonthlyEntries(userId, start, end)
                .stream().map(this::toResponse).toList();
    }

    public Page<AttendanceResponse> getAllAttendance(LocalDate date, Pageable pageable) {
        Page<TimeEntry> page = (date != null)
                ? timeEntryRepository.findByCheckinDateBetween(date, date, pageable)
                : timeEntryRepository.findAll(pageable);
        return page.map(this::toResponse);
    }

    @Transactional
    public TimeExplanationResponse createExplanation(Long userId, TimeExplanationRequest req) {
        TimeEntry entry = timeEntryRepository.findById(req.getTimeEntryId())
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found"));
        if (!entry.getEmployee().getUserId().equals(userId)) {
            throw new BusinessException("Cannot explain another employee's entry");
        }

        TimeExplanation ex = new TimeExplanation();
        ex.setTimeEntry(entry);
        ex.setCreateDate(LocalDate.now());
        ex.setReason(req.getReason());
        ex.setPicture(req.getPicture());
        return toExplainResponse(timeExplanationRepository.save(ex));
    }

    public List<TimeExplanationResponse> getMyExplanations(Long userId) {
        return timeExplanationRepository.findByEmployeeId(userId)
                .stream().map(this::toExplainResponse).toList();
    }

    public Page<TimeExplanationResponse> getPendingExplanations(Long managerId, Pageable pageable) {
        return timeExplanationRepository.findPendingForManager(managerId, pageable)
                .map(this::toExplainResponse);
    }

    @Transactional
    public TimeExplanationResponse approveExplanation(Long explainId, Long approverId, String note) {
        TimeExplanation ex = getExplanationOrThrow(explainId);
        ex.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.APPROVED);
        timeExplanationRepository.save(ex);
        saveConfirmation(ex, approverId, note);
        emailService.sendApprovalResult(ex.getTimeEntry().getEmployee().getEmail(),
                "Time Explanation Approved", "Your explanation has been approved.");
        return toExplainResponse(ex);
    }

    @Transactional
    public TimeExplanationResponse rejectExplanation(Long explainId, Long approverId, String note) {
        TimeExplanation ex = getExplanationOrThrow(explainId);
        ex.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.REJECTED);
        timeExplanationRepository.save(ex);
        saveConfirmation(ex, approverId, note);
        emailService.sendApprovalResult(ex.getTimeEntry().getEmployee().getEmail(),
                "Time Explanation Rejected", "Your explanation has been rejected. Note: " + note);
        return toExplainResponse(ex);
    }

    private void saveConfirmation(TimeExplanation ex, Long approverId, String note) {
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        ConfirmExplanation cf = new ConfirmExplanation();
        cf.setTimeExplanation(ex);
        cf.setApprover(approver);
        cf.setNote(note);
        cf.setCfDate(LocalDate.now());
        confirmExplanationRepository.save(cf);
    }

    private TimeExplanation getExplanationOrThrow(Long id) {
        return timeExplanationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Explanation not found: " + id));
    }

    private AttendanceResponse toResponse(TimeEntry te) {
        String status = "PRESENT";
        if (te.getNumberMinutesLate() != null && te.getNumberMinutesLate() > 0) status = "LATE";
        if (te.getNumberMinutesQuitEarly() != null && te.getNumberMinutesQuitEarly() > 0) status = "EARLY_QUIT";

        return AttendanceResponse.builder()
                .timeEntryId(te.getTimeEntryId())
                .checkinDate(te.getCheckinDate())
                .checkinTime(te.getCheckinTime())
                .checkoutDate(te.getCheckoutDate())
                .checkoutTime(te.getCheckoutTime())
                .minutesLate(te.getNumberMinutesLate())
                .minutesEarly(te.getNumberMinutesQuitEarly())
                .status(status)
                .build();
    }

    private TimeExplanationResponse toExplainResponse(TimeExplanation ex) {
        return TimeExplanationResponse.builder()
                .explainId(ex.getExplainId())
                .timeEntryId(ex.getTimeEntry().getTimeEntryId())
                .checkinDate(ex.getTimeEntry().getCheckinDate())
                .employeeName(ex.getTimeEntry().getEmployee().getFullname())
                .reason(ex.getReason())
                .picture(ex.getPicture())
                .status(ex.getStatus().name())
                .createDate(ex.getCreateDate())
                .build();
    }
}
