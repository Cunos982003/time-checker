package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.dashboard.DashboardResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.LeaveBalance;
import com.apex.timekeeping.domain.repository.*;
import com.apex.timekeeping.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final EmployeeRepository     employeeRepository;
    private final TimeEntryRepository    timeEntryRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WorklogRepository      worklogRepository;

    @Override
    public DashboardResponse getDashboard(Long userId) {
        Employee emp = employeeRepository.findByIdWithDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        LocalDate today = LocalDate.now();
        var todayEntry  = timeEntryRepository.findByEmployee_UserIdAndCheckinDate(userId, today);

        int year = Year.now().getValue();
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployee_UserIdAndYear(userId, year);

        BigDecimal annualAvail = BigDecimal.ZERO;
        BigDecimal sickAvail   = BigDecimal.ZERO;
        for (LeaveBalance lb : balances) {
            BigDecimal avail = lb.getEntitledDays().add(lb.getAdjustedDays())
                    .subtract(lb.getUsedDays()).subtract(lb.getPendingDays());
            if ("ANNUAL".equals(lb.getLeaveType().getTypeCode())) annualAvail = avail;
            if ("SICK".equals(lb.getLeaveType().getTypeCode()))   sickAvail   = avail;
        }

        long pendingLeave    = leaveRequestRepository
                .findPendingForManager(userId, PageRequest.of(0, 1)).getTotalElements();
        long pendingWorklogs = worklogRepository
                .findPendingForManager(userId, PageRequest.of(0, 1)).getTotalElements();

        DashboardResponse.DashboardResponseBuilder builder = DashboardResponse.builder()
                .fullName(emp.getFullname())
                .department(emp.getDepartment() != null ? emp.getDepartment().getDepartName() : null)
                .position(emp.getPosition()     != null ? emp.getPosition().getJobName()      : null)
                .role(emp.getRole()             != null ? emp.getRole().getRoleName()          : null)
                .today(today)
                .annualLeaveAvailable(annualAvail)
                .sickLeaveAvailable(sickAvail)
                .pendingLeaveApprovals(pendingLeave)
                .pendingWorklogApprovals(pendingWorklogs);

        todayEntry.ifPresent(te -> builder
                .checkinTime(te.getCheckinTime())
                .checkoutTime(te.getCheckoutTime())
                .minutesLate(te.getNumberMinutesLate())
                .todayStatus(te.getCheckinTime() != null ? "PRESENT" : "ABSENT"));

        return builder.build();
    }
}
