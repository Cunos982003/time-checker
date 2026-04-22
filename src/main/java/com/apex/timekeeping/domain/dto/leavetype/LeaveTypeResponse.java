package com.apex.timekeeping.domain.dto.leavetype;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LeaveTypeResponse {
    private Long leaveTypeId;
    private String typeCode;
    private String typeName;
    private BigDecimal maxDaysPerYear;
    private Boolean isPaid;
    private Boolean requiresApproval;
    private String description;
    private Boolean isActive;
}
