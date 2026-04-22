package com.apex.timekeeping.domain.dto.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponse {
    private Long userId;
    private String empId;
    private String fullname;
    private String email;
    private String phone;
    private String sex;
    private LocalDate birthday;
    private String status;
    private String roleName;
    private Long roleId;
    private String jobName;
    private Long jobId;
    private String departName;
    private Long departId;
    private String managerName;
    private Long managerId;
}
