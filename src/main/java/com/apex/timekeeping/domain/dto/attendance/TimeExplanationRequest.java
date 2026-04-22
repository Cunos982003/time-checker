package com.apex.timekeeping.domain.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimeExplanationRequest {

    @NotNull
    private Long timeEntryId;

    @NotBlank
    private String reason;

    private String picture;
}
