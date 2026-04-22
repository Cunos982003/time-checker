package com.apex.timekeeping.domain.dto.leavetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeaveTypeRequest {

    @NotBlank
    @Size(max = 30)
    private String typeCode;

    @NotBlank
    @Size(max = 100)
    private String typeName;

    private BigDecimal maxDaysPerYear;
    private Boolean isPaid = true;
    private Boolean requiresApproval = true;
    private String description;
    private Boolean isActive = true;
}
