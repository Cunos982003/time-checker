package com.apex.timekeeping.domain.dto.ot;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class OtResponse {
    private Long otId;
    private LocalDate otDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal workHoursOt;
    private String reason;
    private String status;
    private String typeName;
    private BigDecimal otRate;
    private String projectName;
    private String confirmedByName;
    private LocalDate cfDate;
}
