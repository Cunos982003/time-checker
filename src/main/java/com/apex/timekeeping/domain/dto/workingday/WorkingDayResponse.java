package com.apex.timekeeping.domain.dto.workingday;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class WorkingDayResponse {
    private LocalDate day;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private BigDecimal otRate;
}
