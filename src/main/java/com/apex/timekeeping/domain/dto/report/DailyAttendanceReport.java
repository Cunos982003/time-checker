package com.apex.timekeeping.domain.dto.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class DailyAttendanceReport {
    private Long userId;
    private String empId;
    private String fullName;
    private String department;
    private LocalDate date;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private Integer minutesLate;
    private Integer minutesEarly;
    private String status;
}
