package com.apex.timekeeping.domain.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyAttendanceReport {
    private Long userId;
    private String empId;
    private String fullName;
    private String department;
    private Integer year;
    private Integer month;
    private Integer totalPresent;
    private Integer totalAbsent;
    private Integer totalLate;
    private Integer totalEarlyQuit;
    private BigDecimal totalLeaveDays;
    private BigDecimal totalOtHours;
}
