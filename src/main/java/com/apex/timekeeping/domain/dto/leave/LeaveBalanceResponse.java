package com.apex.timekeeping.domain.dto.leave;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LeaveBalanceResponse {
    private Long balanceId;
    private String leaveTypeName;
    private String typeCode;
    private Integer year;
    private BigDecimal entitledDays;
    private BigDecimal usedDays;
    private BigDecimal pendingDays;
    private BigDecimal adjustedDays;
    private BigDecimal availableDays;
}
