package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.department.DepartmentRequest;
import com.apex.timekeeping.domain.dto.department.DepartmentResponse;

import java.util.List;

public interface IDepartmentService {
    List<DepartmentResponse> findAll();
    DepartmentResponse findById(Long id);
    DepartmentResponse create(DepartmentRequest req);
    DepartmentResponse update(Long id, DepartmentRequest req);
    void delete(Long id);
}
