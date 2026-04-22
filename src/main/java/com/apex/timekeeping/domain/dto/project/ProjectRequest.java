package com.apex.timekeeping.domain.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectRequest {

    @NotBlank
    private String projectCode;

    @NotBlank
    private String projectName;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long managerId;
}
