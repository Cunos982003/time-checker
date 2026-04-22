package com.apex.timekeeping.domain.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class DashboardResponse {
    // Personal info
    private String fullName;
    private String department;
    private String position;
    private String role;

    // Today attendance
    private LocalDate today;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private String todayStatus; // PRESENT, ABSENT, LATE, etc.
    private Integer minutesLate;

    // Pending actions (for manager/staff)
    private Long pendingTimesheetApprovals;
    private Long pendingLeaveApprovals;
    private Long pendingWorklogApprovals;

    // Leave balance summary
    private BigDecimal annualLeaveAvailable;
    private BigDecimal sickLeaveAvailable;
}
