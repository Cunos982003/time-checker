package com.apex.timekeeping.domain.dto.attendance;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TimeExplanationResponse {
    private Long explainId;
    private Long timeEntryId;
    private LocalDate checkinDate;
    private String employeeName;
    private String reason;
    private String picture;
    private String status;
    private LocalDate createDate;
    private String confirmerNote;
    private String confirmerName;
}
