package com.apex.timekeeping.domain.dto.worklog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorklogResponse {
    private Long worklogId;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal workHours;
    private String content;
    private String workType;
    private String status;
    private String projectName;
    private String employeeName;
    private String confirmedByName;
    private String confirmNote;
    private LocalDateTime createdAt;
}
