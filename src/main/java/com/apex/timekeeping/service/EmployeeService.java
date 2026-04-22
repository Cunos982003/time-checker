package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.employee.CreateEmployeeRequest;
import com.apex.timekeeping.domain.dto.employee.EmployeeResponse;
import com.apex.timekeeping.domain.dto.employee.UpdateEmployeeRequest;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.LeaveBalance;
import com.apex.timekeeping.domain.entity.UserAccount;
import com.apex.timekeeping.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userAccountRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final RoleRepository roleRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<EmployeeResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullname"));
        return employeeRepository.findAll(pageable).map(this::toResponse);
    }

    public EmployeeResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest req) {
        if (employeeRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("Email already exists");
        if (employeeRepository.existsByEmpId(req.getEmpId()))
            throw new BusinessException("Employee ID already exists");
        if (userAccountRepository.existsByUsername(req.getEmpId()))
            throw new BusinessException("Username already exists");

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

        // Auto-create user account
        UserAccount ua = new UserAccount();
        ua.setUsername(req.getEmpId());
        ua.setPassword(passwordEncoder.encode(req.getPassword()));
        ua.setEmployee(saved);
        userAccountRepository.save(ua);

        // Initialize leave balances
        initLeaveBalances(saved);

        return toResponse(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest req) {
        Employee emp = getOrThrow(id);
        if (req.getFullname() != null) emp.setFullname(req.getFullname());
        if (req.getPhone() != null) emp.setPhone(req.getPhone());
        if (req.getSex() != null) emp.setSex(req.getSex());
        if (req.getBirthday() != null) emp.setBirthday(req.getBirthday());
        if (req.getStatus() != null) emp.setStatus(req.getStatus());
        if (req.getRoleId() != null)
            emp.setRole(roleRepository.findById(req.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("Role not found")));
        if (req.getDepartId() != null)
            emp.setDepartment(departmentRepository.findById(req.getDepartId()).orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        if (req.getJobId() != null)
            emp.setPosition(positionRepository.findById(req.getJobId()).orElseThrow(() -> new ResourceNotFoundException("Position not found")));
        if (req.getManagerId() != null)
            emp.setManager(employeeRepository.findById(req.getManagerId()).orElseThrow(() -> new ResourceNotFoundException("Manager not found")));
        return toResponse(employeeRepository.save(emp));
    }

    private void initLeaveBalances(Employee emp) {
        int year = java.time.Year.now().getValue();
        leaveTypeRepository.findByIsActiveTrue().forEach(lt -> {
            boolean exists = leaveBalanceRepository
                    .findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(emp.getUserId(), lt.getLeaveTypeId(), year)
                    .isPresent();
            if (!exists) {
                LeaveBalance lb = new LeaveBalance();
                lb.setEmployee(emp);
                lb.setLeaveType(lt);
                lb.setYear(year);
                lb.setEntitledDays(lt.getMaxDaysPerYear() != null ? lt.getMaxDaysPerYear() : java.math.BigDecimal.ZERO);
                leaveBalanceRepository.save(lb);
            }
        });
    }

    private Employee getOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .userId(e.getUserId())
                .empId(e.getEmpId())
                .fullname(e.getFullname())
                .email(e.getEmail())
                .phone(e.getPhone())
                .sex(e.getSex())
                .birthday(e.getBirthday())
                .status(e.getStatus())
                .roleName(e.getRole() != null ? e.getRole().getRoleName() : null)
                .roleId(e.getRole() != null ? e.getRole().getRoleId() : null)
                .jobName(e.getPosition() != null ? e.getPosition().getJobName() : null)
                .jobId(e.getPosition() != null ? e.getPosition().getJobId() : null)
                .departName(e.getDepartment() != null ? e.getDepartment().getDepartName() : null)
                .departId(e.getDepartment() != null ? e.getDepartment().getDepartId() : null)
                .managerName(e.getManager() != null ? e.getManager().getFullname() : null)
                .managerId(e.getManager() != null ? e.getManager().getUserId() : null)
                .build();
    }
}
