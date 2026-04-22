package com.apex.timekeeping.domain.dto.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PositionRequest {

    @NotBlank
    @Size(max = 20)
    private String jobCode;

    @NotBlank
    @Size(max = 100)
    private String jobName;

    @Size(max = 50)
    private String level;

    @Size(max = 500)
    private String jobDescription;
}
