package com.apex.timekeeping.domain.dto.ot;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class OtRequest {

    @NotNull
    private LocalDate otDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private String reason;

    @NotNull
    private Long typeId;

    private Long projectId;
}
