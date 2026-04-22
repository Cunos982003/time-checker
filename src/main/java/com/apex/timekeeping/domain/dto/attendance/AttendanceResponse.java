package com.apex.timekeeping.domain.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long timeEntryId;
    private LocalDate checkinDate;
    private LocalTime checkinTime;
    private LocalDate checkoutDate;
    private LocalTime checkoutTime;
    private Integer minutesLate;
    private Integer minutesEarly;
    private String status; // PRESENT, LATE, EARLY_QUIT, ABSENT, WEEKEND, HOLIDAY
}
