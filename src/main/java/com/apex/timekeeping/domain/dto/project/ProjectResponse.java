package com.apex.timekeeping.domain.dto.project;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProjectResponse {
    private Long projectId;
    private String projectCode;
    private String projectName;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String managerName;
    private Long managerId;
}
