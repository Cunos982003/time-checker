package com.apex.timekeeping.domain.dto.worklog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WorklogRequest {

    @NotNull
    private LocalDate workDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @NotNull
    private BigDecimal workHours;

    @NotBlank
    private String content;

    private String workType = "NORMAL"; // NORMAL or OT

    private Long projectId;
}
