package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.domain.dto.attendance.AttendanceResponse;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationRequest;
import com.apex.timekeeping.domain.dto.attendance.TimeExplanationResponse;
import com.apex.timekeeping.domain.entity.ConfirmExplanation;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.TimeEntry;
import com.apex.timekeeping.domain.entity.TimeExplanation;
import com.apex.timekeeping.domain.repository.*;
import com.apex.timekeeping.service.IAttendanceService;
import com.apex.timekeeping.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService {

    private final TimeEntryRepository           timeEntryRepository;
    private final WorkingDayRepository          workingDayRepository;
    private final TimeExplanationRepository     timeExplanationRepository;
    private final ConfirmExplanationRepository  confirmExplanationRepository;
    private final EmployeeRepository            employeeRepository;
    private final EmailService                  emailService;

    @Override
    @Transactional
    public AttendanceResponse checkIn(Long userId) {
        if (timeEntryRepository.existsByEmployee_UserIdAndCheckinDate(userId, LocalDate.now()))
            throw new BusinessException("Already checked in today");
        Employee emp = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        TimeEntry te = new TimeEntry();
        te.setEmployee(emp);
        te.setCheckinDate(LocalDate.now());
        te.setCheckinTime(LocalTime.now());
        workingDayRepository.findById(LocalDate.now()).ifPresent(wd -> {
            if (wd.getCheckinTime() != null && LocalTime.now().isAfter(wd.getCheckinTime())) {
                long late = java.time.Duration.between(wd.getCheckinTime(), LocalTime.now()).toMinutes();
                te.setNumberMinutesLate((int) late);
            }
        });
        return toAttendanceResponse(timeEntryRepository.save(te));
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(Long userId) {
        TimeEntry te = timeEntryRepository.findByEmployee_UserIdAndCheckinDate(userId, LocalDate.now())
                .orElseThrow(() -> new BusinessException("No check-in record found for today"));
        te.setCheckoutTime(LocalTime.now());
        workingDayRepository.findById(LocalDate.now()).ifPresent(wd -> {
            if (wd.getCheckoutTime() != null && LocalTime.now().isBefore(wd.getCheckoutTime())) {
                long early = java.time.Duration.between(LocalTime.now(), wd.getCheckoutTime()).toMinutes();
                te.setNumberMinutesQuitEarly((int) early);
            }
        });
        return toAttendanceResponse(timeEntryRepository.save(te));
    }

    @Override
    public AttendanceResponse getTodayStatus(Long userId) {
        return timeEntryRepository.findByEmployee_UserIdAndCheckinDate(userId, LocalDate.now())
                .map(this::toAttendanceResponse)
                .orElse(AttendanceResponse.builder().status("ABSENT").build());
    }

    @Override
    public List<AttendanceResponse> getMonthlyAttendance(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());
        return timeEntryRepository.findByEmployee_UserIdAndCheckinDateBetweenOrderByCheckinDate(userId, start, end)
                .stream().map(this::toAttendanceResponse).toList();
    }

    @Override
    public Page<AttendanceResponse> getAllAttendance(LocalDate date, Pageable pageable) {
        LocalDate from = (date != null) ? date : LocalDate.now().withDayOfMonth(1);
        LocalDate to   = (date != null) ? date : LocalDate.now();
        return timeEntryRepository.findByCheckinDateBetween(from, to, pageable)
                .map(this::toAttendanceResponse);
    }

    @Override
    @Transactional
    public TimeExplanationResponse createExplanation(Long userId, TimeExplanationRequest req) {
        TimeEntry te = timeEntryRepository.findById(req.getTimeEntryId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
        if (!te.getEmployee().getUserId().equals(userId))
            throw new BusinessException("This attendance record does not belong to you");
        TimeExplanation ex = new TimeExplanation();
        ex.setTimeEntry(te);
        ex.setReason(req.getReason());
        ex.setPicture(req.getPicture());
        ex.setCreateDate(LocalDate.now());
        return toExplanationResponse(timeExplanationRepository.save(ex));
    }

    @Override
    public List<TimeExplanationResponse> getMyExplanations(Long userId) {
        return timeExplanationRepository.findByEmployeeId(userId)
                .stream().map(this::toExplanationResponse).toList();
    }

    @Override
    public Page<TimeExplanationResponse> getPendingExplanations(Long managerId, Pageable pageable) {
        return timeExplanationRepository.findPendingForManager(managerId, pageable)
                .map(this::toExplanationResponse);
    }

    @Override
    @Transactional
    public TimeExplanationResponse approveExplanation(Long explanationId, Long approverId, String note) {
        TimeExplanation ex = getExplanationOrThrow(explanationId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        ex.setStatus(ApprovalStatus.APPROVED);
        timeExplanationRepository.save(ex);
        saveConfirmExplanation(ex, approver, note);
        emailService.sendApprovalResult(ex.getTimeEntry().getEmployee().getEmail(),
                "Time Explanation Approved",
                "Your time explanation has been approved.");
        return toExplanationResponse(ex);
    }

    @Override
    @Transactional
    public TimeExplanationResponse rejectExplanation(Long explanationId, Long approverId, String note) {
        TimeExplanation ex = getExplanationOrThrow(explanationId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        ex.setStatus(ApprovalStatus.REJECTED);
        timeExplanationRepository.save(ex);
        saveConfirmExplanation(ex, approver, note);
        emailService.sendApprovalResult(ex.getTimeEntry().getEmployee().getEmail(),
                "Time Explanation Rejected",
                "Your time explanation has been rejected. Note: " + note);
        return toExplanationResponse(ex);
    }

    // ===================== PRIVATE HELPERS =====================

    private void saveConfirmExplanation(TimeExplanation ex, Employee approver, String note) {
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

    private AttendanceResponse toAttendanceResponse(TimeEntry te) {
        String status = "PRESENT";
        if (te.getNumberMinutesLate()      != null && te.getNumberMinutesLate()      > 0) status = "LATE";
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

    private TimeExplanationResponse toExplanationResponse(TimeExplanation ex) {
        ConfirmExplanation cf = ex.getConfirmExplanation();
        return TimeExplanationResponse.builder()
                .explainId(ex.getExplainId())
                .timeEntryId(ex.getTimeEntry() != null ? ex.getTimeEntry().getTimeEntryId() : null)
                .checkinDate(ex.getTimeEntry() != null ? ex.getTimeEntry().getCheckinDate() : null)
                .employeeName(ex.getTimeEntry() != null && ex.getTimeEntry().getEmployee() != null
                        ? ex.getTimeEntry().getEmployee().getFullname() : null)
                .reason(ex.getReason())
                .picture(ex.getPicture())
                .status(ex.getStatus() != null ? ex.getStatus().name() : null)
                .createDate(ex.getCreateDate())
                .confirmerNote(cf != null ? cf.getNote() : null)
                .confirmerName(cf != null && cf.getApprover() != null ? cf.getApprover().getFullname() : null)
                .build();
    }
}
