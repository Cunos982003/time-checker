package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestDto;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.LeaveBalance;
import com.apex.timekeeping.domain.entity.LeaveRequest;
import com.apex.timekeeping.domain.entity.LeaveType;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import com.apex.timekeeping.domain.repository.LeaveBalanceRepository;
import com.apex.timekeeping.domain.repository.LeaveRequestRepository;
import com.apex.timekeeping.domain.repository.LeaveTypeRepository;
import com.apex.timekeeping.service.impl.LeaveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock private LeaveRequestRepository leaveRequestRepository;
    @Mock private LeaveBalanceRepository leaveBalanceRepository;
    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmailService emailService;
    @Mock private ModelMapper modelMapper;

    private LeaveServiceImpl leaveService;

    private Employee employee;
    private LeaveType leaveType;
    private LeaveBalance balance;

    @BeforeEach
    void setUp() {
        // Constructor order matches field declaration in LeaveServiceImpl:
        // leaveRequestRepository, leaveBalanceRepository, leaveTypeRepository,
        // employeeRepository, emailService, modelMapper
        leaveService = new LeaveServiceImpl(
                leaveRequestRepository, leaveBalanceRepository,
                leaveTypeRepository, employeeRepository,
                emailService, modelMapper);

        employee = new Employee();
        employee.setUserId(1L);
        employee.setFullname("Test User");
        employee.setEmail("test@example.com");

        leaveType = new LeaveType();
        leaveType.setLeaveTypeId(1L);
        leaveType.setTypeCode("ANNUAL");
        leaveType.setTypeName("Annual Leave");

        balance = new LeaveBalance();
        balance.setBalanceId(1L);
        balance.setEmployee(employee);
        balance.setLeaveType(leaveType);
        balance.setYear(LocalDate.now().getYear());
        balance.setEntitledDays(BigDecimal.valueOf(12));
        balance.setUsedDays(BigDecimal.ZERO);
        balance.setPendingDays(BigDecimal.ZERO);
        balance.setAdjustedDays(BigDecimal.ZERO);
    }

    @Test
    void create_success() {
        LeaveRequestDto req = new LeaveRequestDto();
        req.setLeaveTypeId(1L);
        req.setStartDate(LocalDate.now().plusDays(5));
        req.setEndDate(LocalDate.now().plusDays(6));
        req.setReason("Vacation");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.findOverlapping(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(leaveBalanceRepository.findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(anyLong(), anyLong(), anyInt()))
                .thenReturn(Optional.of(balance));

        LeaveRequest saved = new LeaveRequest();
        saved.setRequestId(100L);
        saved.setEmployee(employee);
        saved.setLeaveType(leaveType);
        saved.setStartDate(req.getStartDate());
        saved.setEndDate(req.getEndDate());
        saved.setTotalDays(BigDecimal.valueOf(2));
        saved.setReason(req.getReason());
        saved.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.PENDING);
        when(leaveRequestRepository.save(any())).thenReturn(saved);

        LeaveRequestResponse mapped = new LeaveRequestResponse();
        mapped.setRequestId(100L);
        when(modelMapper.map(any(LeaveRequest.class), eq(LeaveRequestResponse.class))).thenReturn(mapped);

        LeaveRequestResponse response = leaveService.create(1L, req);

        assertThat(response).isNotNull();
        assertThat(response.getRequestId()).isEqualTo(100L);
    }

    @Test
    void create_insufficientBalance_throws() {
        balance.setUsedDays(BigDecimal.valueOf(12)); // exhausted

        LeaveRequestDto req = new LeaveRequestDto();
        req.setLeaveTypeId(1L);
        req.setStartDate(LocalDate.now().plusDays(5));
        req.setEndDate(LocalDate.now().plusDays(7));
        req.setReason("Over budget");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.findOverlapping(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(leaveBalanceRepository.findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(anyLong(), anyLong(), anyInt()))
                .thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> leaveService.create(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient leave balance");
    }

    @Test
    void create_overlapping_throws() {
        LeaveRequestDto req = new LeaveRequestDto();
        req.setLeaveTypeId(1L);
        req.setStartDate(LocalDate.now().plusDays(1));
        req.setEndDate(LocalDate.now().plusDays(2));
        req.setReason("Overlap");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));

        LeaveRequest overlapping = new LeaveRequest();
        when(leaveRequestRepository.findOverlapping(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(overlapping));

        assertThatThrownBy(() -> leaveService.create(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("overlap");
    }
}
