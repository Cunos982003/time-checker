package com.apex.timekeeping.domain.dto.workingday;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WorkingDayRequest {
    private LocalDate day;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private BigDecimal otRate;
}
