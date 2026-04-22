package com.apex.timekeeping.domain.dto.employee;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEmployeeRequest {

    @NotBlank
    @Size(max = 20)
    private String empId;

    @NotBlank
    @Size(max = 150)
    private String fullname;

    @NotBlank
    @Email
    private String email;

    private String phone;
    private String sex;
    private LocalDate birthday;

    @NotNull
    private Long roleId;

    private Long jobId;

    @NotNull
    private Long departId;

    private Long managerId;

    @NotBlank
    @Size(min = 6)
    private String password;
}
