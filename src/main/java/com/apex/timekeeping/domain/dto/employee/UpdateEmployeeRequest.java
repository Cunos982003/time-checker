package com.apex.timekeeping.domain.dto.employee;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmployeeRequest {

    @Size(max = 150)
    private String fullname;

    @Email
    private String email;

    private String phone;
    private String sex;
    private LocalDate birthday;
    private Long roleId;
    private Long jobId;
    private Long departId;
    private Long managerId;
    private String status;
}
