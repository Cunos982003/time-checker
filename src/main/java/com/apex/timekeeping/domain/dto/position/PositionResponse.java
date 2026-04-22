package com.apex.timekeeping.domain.dto.position;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PositionResponse {
    private Long jobId;
    private String jobCode;
    private String jobName;
    private String level;
    private String jobDescription;
}
