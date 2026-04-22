package com.apex.timekeeping.domain.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentRequest {

    @NotBlank
    @Size(max = 20)
    private String departCode;

    @NotBlank
    @Size(max = 100)
    private String departName;

    @Size(max = 500)
    private String departDescription;

    private Long managerId;
}
