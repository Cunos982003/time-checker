package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.ot.OtRequest;
import com.apex.timekeeping.domain.dto.ot.OtResponse;
import com.apex.timekeeping.domain.entity.*;
import com.apex.timekeeping.domain.repository.*;
import com.apex.timekeeping.service.IOtService;
import com.apex.timekeeping.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OtServiceImpl implements IOtService {

    private final OtRepository           otRepository;
    private final OtTypeRepository       otTypeRepository;
    private final ProjectRepository      projectRepository;
    private final EmployeeRepository     employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository    leaveTypeRepository;
    private final EmailService           emailService;
    private final ModelMapper            modelMapper;

    @Override
    @Transactional
    public OtResponse create(Long userId, OtRequest req) {
        Employee emp  = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        OtType   type = otTypeRepository.findById(req.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("OT type not found"));

        Ot ot = new Ot();
        ot.setEmployee(emp);
        ot.setOtType(type);
        ot.setOtDate(req.getOtDate());
        ot.setStartTime(req.getStartTime());
        ot.setEndTime(req.getEndTime());
        ot.setReason(req.getReason());

        long minutes = Duration.between(req.getStartTime(), req.getEndTime()).toMinutes();
        ot.setWorkHoursOt(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));

        if (req.getProjectId() != null)
            ot.setProject(projectRepository.findById(req.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found")));

        return toOtResponse(otRepository.save(ot));
    }

    @Override
    public List<OtResponse> getMyOt(Long userId) {
        return otRepository.findByEmployee_UserIdOrderByOtDateDesc(userId)
                .stream().map(this::toOtResponse).toList();
    }

    @Override
    public Page<OtResponse> getPendingForManager(Long managerId, Pageable pageable) {
        return otRepository.findPendingForManager(managerId, pageable).map(this::toOtResponse);
    }

    @Override
    @Transactional
    public OtResponse approve(Long otId, Long approverId) {
        Ot ot = getOrThrow(otId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        ot.setStatus(ApprovalStatus.APPROVED);
        ot.setConfirmedBy(approver);
        ot.setCfDate(LocalDate.now());
        emailService.sendApprovalResult(ot.getEmployee().getEmail(), "OT Approved",
                "Your OT on " + ot.getOtDate() + " has been approved.");
        return toOtResponse(otRepository.save(ot));
    }

    @Override
    @Transactional
    public OtResponse reject(Long otId, Long approverId) {
        Ot ot = getOrThrow(otId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        ot.setStatus(ApprovalStatus.REJECTED);
        ot.setConfirmedBy(approver);
        emailService.sendApprovalResult(ot.getEmployee().getEmail(), "OT Rejected",
                "Your OT request on " + ot.getOtDate() + " has been rejected.");
        return toOtResponse(otRepository.save(ot));
    }

    @Override
    @Transactional
    public void convertToLeave(Long otId) {
        Ot ot = getOrThrow(otId);
        if (ot.getStatus() != ApprovalStatus.APPROVED)
            throw new BusinessException("Only approved OT can be converted");

        BigDecimal leaveDays = ot.getWorkHoursOt().divide(BigDecimal.valueOf(8), 1, RoundingMode.DOWN);
        if (leaveDays.compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("OT hours insufficient to convert to leave day");

        LeaveType compType = leaveTypeRepository.findByTypeCode("COMPENSATORY")
                .orElseThrow(() -> new ResourceNotFoundException("Compensatory leave type not found"));

        int year = Year.now().getValue();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(
                        ot.getEmployee().getUserId(), compType.getLeaveTypeId(), year)
                .orElseGet(() -> {
                    LeaveBalance lb = new LeaveBalance();
                    lb.setEmployee(ot.getEmployee());
                    lb.setLeaveType(compType);
                    lb.setYear(year);
                    return leaveBalanceRepository.save(lb);
                });

        balance.setAdjustedDays(balance.getAdjustedDays().add(leaveDays));
        leaveBalanceRepository.save(balance);
        ot.setStatus(ApprovalStatus.CONVERTED);
        otRepository.save(ot);
    }

    // ===================== PRIVATE HELPERS =====================

    private Ot getOrThrow(Long id) {
        return otRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OT record not found: " + id));
    }

    private OtResponse toOtResponse(Ot ot) {
        OtResponse res = modelMapper.map(ot, OtResponse.class);
        res.setStatus(ot.getStatus().name());
        res.setTypeName(ot.getOtType().getTypeName());
        res.setOtRate(ot.getOtType().getOtRate());
        res.setProjectName(ot.getProject()      != null ? ot.getProject().getProjectName()     : null);
        res.setConfirmedByName(ot.getConfirmedBy() != null ? ot.getConfirmedBy().getFullname() : null);
        return res;
    }
}
