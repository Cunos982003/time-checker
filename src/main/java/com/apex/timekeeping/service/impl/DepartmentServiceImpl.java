package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.department.DepartmentRequest;
import com.apex.timekeeping.domain.dto.department.DepartmentResponse;
import com.apex.timekeeping.domain.entity.Department;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.repository.DepartmentRepository;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import com.apex.timekeeping.service.IDepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements IDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository   employeeRepository;
    private final ModelMapper          modelMapper;

    @Override
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll().stream()
                .map(this::toDepartmentResponse).toList();
    }

    @Override
    public DepartmentResponse findById(Long id) {
        return toDepartmentResponse(getOrThrow(id));
    }

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest req) {
        if (departmentRepository.existsByDepartCode(req.getDepartCode()))
            throw new BusinessException("Department code already exists");
        Department d = new Department();
        applyRequest(d, req);
        return toDepartmentResponse(departmentRepository.save(d));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest req) {
        Department d = getOrThrow(id);
        applyRequest(d, req);
        return toDepartmentResponse(departmentRepository.save(d));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        departmentRepository.delete(getOrThrow(id));
    }

    // ===================== PRIVATE HELPERS =====================

    private void applyRequest(Department d, DepartmentRequest req) {
        modelMapper.map(req, d);
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

    private DepartmentResponse toDepartmentResponse(Department d) {
        DepartmentResponse res = modelMapper.map(d, DepartmentResponse.class);
        res.setManagerId(d.getManager()   != null ? d.getManager().getUserId()   : null);
        res.setManagerName(d.getManager() != null ? d.getManager().getFullname() : null);
        return res;
    }
}
