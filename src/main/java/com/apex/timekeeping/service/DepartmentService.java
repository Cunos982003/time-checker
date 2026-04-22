package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.department.DepartmentRequest;
import com.apex.timekeeping.domain.dto.department.DepartmentResponse;
import com.apex.timekeeping.domain.entity.Department;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.repository.DepartmentRepository;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DepartmentResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest req) {
        if (departmentRepository.existsByDepartCode(req.getDepartCode())) {
            throw new BusinessException("Department code already exists");
        }
        Department d = new Department();
        applyRequest(d, req);
        return toResponse(departmentRepository.save(d));
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest req) {
        Department d = getOrThrow(id);
        applyRequest(d, req);
        return toResponse(departmentRepository.save(d));
    }

    @Transactional
    public void delete(Long id) {
        departmentRepository.delete(getOrThrow(id));
    }

    private void applyRequest(Department d, DepartmentRequest req) {
        d.setDepartCode(req.getDepartCode());
        d.setDepartName(req.getDepartName());
        d.setDepartDescription(req.getDepartDescription());
        if (req.getManagerId() != null) {
            Employee mgr = employeeRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            d.setManager(mgr);
        }
    }

    private Department getOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    private DepartmentResponse toResponse(Department d) {
        return DepartmentResponse.builder()
                .departId(d.getDepartId())
                .departCode(d.getDepartCode())
                .departName(d.getDepartName())
                .departDescription(d.getDepartDescription())
                .status(d.getStatus())
                .managerId(d.getManager() != null ? d.getManager().getUserId() : null)
                .managerName(d.getManager() != null ? d.getManager().getFullname() : null)
                .build();
    }
}
