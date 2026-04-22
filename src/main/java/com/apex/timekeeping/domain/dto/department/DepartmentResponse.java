package com.apex.timekeeping.domain.dto.department;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponse {
    private Long departId;
    private String departCode;
    private String departName;
    private String departDescription;
    private String status;
    private Long managerId;
    private String managerName;
}
