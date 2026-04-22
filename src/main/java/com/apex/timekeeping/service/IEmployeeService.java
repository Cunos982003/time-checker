package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.employee.CreateEmployeeRequest;
import com.apex.timekeeping.domain.dto.employee.EmployeeResponse;
import com.apex.timekeeping.domain.dto.employee.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;

public interface IEmployeeService {
    Page<EmployeeResponse> findAll(int page, int size);
    EmployeeResponse findById(Long id);
    EmployeeResponse create(CreateEmployeeRequest req);
    EmployeeResponse update(Long id, UpdateEmployeeRequest req);
}
