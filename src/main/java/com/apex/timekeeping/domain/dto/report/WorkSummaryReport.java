package com.apex.timekeeping.domain.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkSummaryReport {
    private String groupName;   // department name or project name
    private Long groupId;
    private Integer totalMembers;
    private Integer totalWorklogs;
    private BigDecimal totalHours;
    private Integer pendingWorklogs;
    private Integer approvedWorklogs;
}
