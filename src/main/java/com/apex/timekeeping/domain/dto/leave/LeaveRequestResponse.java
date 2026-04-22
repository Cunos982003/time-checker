package com.apex.timekeeping.domain.dto.leave;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LeaveRequestResponse {
    private Long requestId;
    private String employeeName;
    private Long employeeId;
    private String leaveTypeName;
    private Long leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDays;
    private String reason;
    private String status;
    private String approverName;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
