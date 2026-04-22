package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.leave.LeaveBalanceResponse;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestDto;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.LeaveBalance;
import com.apex.timekeeping.domain.entity.LeaveRequest;
import com.apex.timekeeping.domain.entity.LeaveType;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import com.apex.timekeeping.domain.repository.LeaveBalanceRepository;
import com.apex.timekeeping.domain.repository.LeaveRequestRepository;
import com.apex.timekeeping.domain.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    public List<LeaveBalanceResponse> getMyBalances(Long userId) {
        int year = Year.now().getValue();
        return leaveBalanceRepository.findByEmployee_UserIdAndYear(userId, year)
                .stream().map(this::toBalanceResponse).toList();
    }

    @Transactional
    public LeaveRequestResponse create(Long userId, LeaveRequestDto req) {
        Employee emp = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        LeaveType lt = leaveTypeRepository.findById(req.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        validateNoOverlap(userId, req.getStartDate(), req.getEndDate(), null);

        BigDecimal totalDays = countBusinessDays(req.getStartDate(), req.getEndDate());

        // Check balance
        int year = Year.now().getValue();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(userId, lt.getLeaveTypeId(), year)
                .orElseThrow(() -> new BusinessException("No leave balance found for this type"));

        BigDecimal available = balance.getEntitledDays()
                .add(balance.getAdjustedDays())
                .subtract(balance.getUsedDays())
                .subtract(balance.getPendingDays());
        if (available.compareTo(totalDays) < 0) {
            throw new BusinessException("Insufficient leave balance. Available: " + available);
        }

        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(emp);
        lr.setLeaveType(lt);
        lr.setStartDate(req.getStartDate());
        lr.setEndDate(req.getEndDate());
        lr.setTotalDays(totalDays);
        lr.setReason(req.getReason());

        LeaveRequest saved = leaveRequestRepository.save(lr);

        // Reserve pending days
        balance.setPendingDays(balance.getPendingDays().add(totalDays));
        leaveBalanceRepository.save(balance);

        return toResponse(saved);
    }

    @Transactional
    public LeaveRequestResponse cancel(Long requestId, Long userId) {
        LeaveRequest lr = getOrThrow(requestId);
        if (!lr.getEmployee().getUserId().equals(userId))
            throw new BusinessException("Cannot cancel another employee's request");
        if (lr.getStatus() == com.apex.timekeeping.common.enums.ApprovalStatus.APPROVED)
            throw new BusinessException("Cannot cancel an approved request");

        releaseBalance(lr);
        lr.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.CANCELLED);
        return toResponse(leaveRequestRepository.save(lr));
    }

    @Transactional
    public LeaveRequestResponse approve(Long requestId, Long approverId) {
        LeaveRequest lr = getOrThrow(requestId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));

        // Convert pending -> used
        updateBalance(lr, false);
        lr.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.APPROVED);
        lr.setApprovedBy(approver);
        LeaveRequest saved = leaveRequestRepository.save(lr);
        emailService.sendApprovalResult(lr.getEmployee().getEmail(), "Leave Approved",
                "Your leave from " + lr.getStartDate() + " to " + lr.getEndDate() + " has been approved.");
        return toResponse(saved);
    }

    @Transactional
    public LeaveRequestResponse reject(Long requestId, Long approverId, String reason) {
        LeaveRequest lr = getOrThrow(requestId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));

        releaseBalance(lr);
        lr.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.REJECTED);
        lr.setApprovedBy(approver);
        lr.setRejectionReason(reason);
        LeaveRequest saved = leaveRequestRepository.save(lr);
        emailService.sendApprovalResult(lr.getEmployee().getEmail(), "Leave Rejected",
                "Your leave request has been rejected. Reason: " + reason);
        return toResponse(saved);
    }

    public List<LeaveRequestResponse> getMyRequests(Long userId) {
        return leaveRequestRepository.findByEmployee_UserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public Page<LeaveRequestResponse> getPendingForManager(Long managerId, Pageable pageable) {
        return leaveRequestRepository.findPendingForManager(managerId, pageable).map(this::toResponse);
    }

    private void updateBalance(LeaveRequest lr, boolean isPending) {
        int year = lr.getStartDate().getYear();
        leaveBalanceRepository.findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(
                lr.getEmployee().getUserId(), lr.getLeaveType().getLeaveTypeId(), year)
                .ifPresent(b -> {
                    b.setPendingDays(b.getPendingDays().subtract(lr.getTotalDays()));
                    b.setUsedDays(b.getUsedDays().add(lr.getTotalDays()));
                    leaveBalanceRepository.save(b);
                });
    }

    private void releaseBalance(LeaveRequest lr) {
        if (lr.getStatus() == com.apex.timekeeping.common.enums.ApprovalStatus.PENDING) {
            int year = lr.getStartDate().getYear();
            leaveBalanceRepository.findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(
                    lr.getEmployee().getUserId(), lr.getLeaveType().getLeaveTypeId(), year)
                    .ifPresent(b -> {
                        b.setPendingDays(b.getPendingDays().subtract(lr.getTotalDays()));
                        leaveBalanceRepository.save(b);
                    });
        }
    }

    private void validateNoOverlap(Long userId, LocalDate start, LocalDate end, Long excludeId) {
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlapping(userId, start, end);
        if (excludeId != null) overlapping = overlapping.stream()
                .filter(r -> !r.getRequestId().equals(excludeId)).toList();
        if (!overlapping.isEmpty())
            throw new BusinessException("Leave dates overlap with an existing request");
    }

    private BigDecimal countBusinessDays(LocalDate start, LocalDate end) {
        long days = start.datesUntil(end.plusDays(1))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
        return BigDecimal.valueOf(days);
    }

    private LeaveRequest getOrThrow(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));
    }

    private LeaveRequestResponse toResponse(LeaveRequest lr) {
        return LeaveRequestResponse.builder()
                .requestId(lr.getRequestId())
                .employeeName(lr.getEmployee().getFullname())
                .employeeId(lr.getEmployee().getUserId())
                .leaveTypeName(lr.getLeaveType().getTypeName())
                .leaveTypeId(lr.getLeaveType().getLeaveTypeId())
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .totalDays(lr.getTotalDays())
                .reason(lr.getReason())
                .status(lr.getStatus().name())
                .approverName(lr.getApprovedBy() != null ? lr.getApprovedBy().getFullname() : null)
                .rejectionReason(lr.getRejectionReason())
                .createdAt(lr.getCreatedAt())
                .build();
    }

    private LeaveBalanceResponse toBalanceResponse(LeaveBalance lb) {
        BigDecimal available = lb.getEntitledDays().add(lb.getAdjustedDays())
                .subtract(lb.getUsedDays()).subtract(lb.getPendingDays());
        return LeaveBalanceResponse.builder()
                .balanceId(lb.getBalanceId())
                .leaveTypeName(lb.getLeaveType().getTypeName())
                .typeCode(lb.getLeaveType().getTypeCode())
                .year(lb.getYear())
                .entitledDays(lb.getEntitledDays())
                .usedDays(lb.getUsedDays())
                .pendingDays(lb.getPendingDays())
                .adjustedDays(lb.getAdjustedDays())
                .availableDays(available)
                .build();
    }
}
