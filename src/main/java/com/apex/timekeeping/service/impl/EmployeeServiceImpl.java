package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.employee.CreateEmployeeRequest;
import com.apex.timekeeping.domain.dto.employee.EmployeeResponse;
import com.apex.timekeeping.domain.dto.employee.UpdateEmployeeRequest;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.LeaveBalance;
import com.apex.timekeeping.domain.entity.UserAccount;
import com.apex.timekeeping.domain.repository.*;
import com.apex.timekeeping.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepository     employeeRepository;
    private final UserAccountRepository  userAccountRepository;
    private final DepartmentRepository   departmentRepository;
    private final PositionRepository     positionRepository;
    private final RoleRepository         roleRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository    leaveTypeRepository;
    private final PasswordEncoder        passwordEncoder;
    private final ModelMapper            modelMapper;

    @Override
    public Page<EmployeeResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullname"));
        return employeeRepository.findAll(pageable).map(this::toEmployeeResponse);
    }

    @Override
    public EmployeeResponse findById(Long id) {
        return toEmployeeResponse(getOrThrow(id));
    }

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest req) {
        if (employeeRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("Email already exists");
        if (employeeRepository.existsByEmpId(req.getEmpId()))
            throw new BusinessException("Employee ID already exists");

        Employee emp = new Employee();
        emp.setEmpId(req.getEmpId());
        emp.setFullname(req.getFullname());
        emp.setEmail(req.getEmail());
        emp.setPhone(req.getPhone());
        emp.setSex(req.getSex());
        emp.setBirthday(req.getBirthday());

        emp.setRole(roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found")));
        emp.setDepartment(departmentRepository.findById(req.getDepartId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        if (req.getJobId() != null)
            emp.setPosition(positionRepository.findById(req.getJobId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position not found")));
        if (req.getManagerId() != null)
            emp.setManager(employeeRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found")));

        Employee saved = employeeRepository.save(emp);

        UserAccount ua = new UserAccount();
        ua.setUsername(req.getEmpId());
        ua.setPassword(passwordEncoder.encode(req.getPassword()));
        ua.setEmployee(saved);
        userAccountRepository.save(ua);

        initLeaveBalances(saved);
        return toEmployeeResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest req) {
        Employee emp = getOrThrow(id);
        modelMapper.map(req, emp);
        if (req.getRoleId() != null)
            emp.setRole(roleRepository.findById(req.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found")));
        if (req.getDepartId() != null)
            emp.setDepartment(departmentRepository.findById(req.getDepartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        if (req.getJobId() != null)
            emp.setPosition(positionRepository.findById(req.getJobId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position not found")));
        if (req.getManagerId() != null)
            emp.setManager(employeeRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found")));
        return toEmployeeResponse(employeeRepository.save(emp));
    }

    // ===================== PRIVATE HELPERS =====================

    private void initLeaveBalances(Employee emp) {
        int year = Year.now().getValue();
        leaveTypeRepository.findByIsActiveTrue().forEach(lt -> {
            boolean exists = leaveBalanceRepository
                    .findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(
                            emp.getUserId(), lt.getLeaveTypeId(), year)
                    .isPresent();
            if (!exists) {
                LeaveBalance lb = new LeaveBalance();
                lb.setEmployee(emp);
                lb.setLeaveType(lt);
                lb.setYear(year);
                lb.setEntitledDays(lt.getMaxDaysPerYear() != null
                        ? lt.getMaxDaysPerYear() : BigDecimal.ZERO);
                leaveBalanceRepository.save(lb);
            }
        });
    }

    private Employee getOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private EmployeeResponse toEmployeeResponse(Employee e) {
        EmployeeResponse res = modelMapper.map(e, EmployeeResponse.class);
        res.setRoleId(e.getRole()           != null ? e.getRole().getRoleId()             : null);
        res.setRoleName(e.getRole()         != null ? e.getRole().getRoleName()            : null);
        res.setJobId(e.getPosition()        != null ? e.getPosition().getJobId()           : null);
        res.setJobName(e.getPosition()      != null ? e.getPosition().getJobName()         : null);
        res.setDepartId(e.getDepartment()   != null ? e.getDepartment().getDepartId()      : null);
        res.setDepartName(e.getDepartment() != null ? e.getDepartment().getDepartName()    : null);
        res.setManagerId(e.getManager()     != null ? e.getManager().getUserId()           : null);
        res.setManagerName(e.getManager()   != null ? e.getManager().getFullname()         : null);
        return res;
    }
}
