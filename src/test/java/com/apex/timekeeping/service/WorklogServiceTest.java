package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.domain.dto.worklog.WorklogRequest;
import com.apex.timekeeping.domain.dto.worklog.WorklogResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.Worklog;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import com.apex.timekeeping.domain.repository.ProjectRepository;
import com.apex.timekeeping.domain.repository.WorklogConfirmationRepository;
import com.apex.timekeeping.domain.repository.WorklogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorklogServiceTest {

    @Mock private WorklogRepository worklogRepository;
    @Mock private WorklogConfirmationRepository worklogConfirmationRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks
    private WorklogService worklogService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setUserId(1L);
        employee.setFullname("Test User");
    }

    @Test
    void create_success() {
        WorklogRequest req = new WorklogRequest();
        req.setWorkDate(LocalDate.now());
        req.setStartTime(LocalTime.of(9, 0));
        req.setEndTime(LocalTime.of(17, 0));
        req.setContent("Implemented feature X");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Worklog saved = new Worklog();
        saved.setWorklogId(5L);
        saved.setEmployee(employee);
        saved.setWorkDate(req.getWorkDate());
        saved.setContent(req.getContent());
        saved.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.PENDING);
        saved.setWorkHours(BigDecimal.valueOf(8));
        when(worklogRepository.save(any())).thenReturn(saved);

        WorklogResponse response = worklogService.create(1L, req);

        assertThat(response).isNotNull();
        assertThat(response.getWorklogId()).isEqualTo(5L);
        verify(worklogRepository).save(any(Worklog.class));
    }

    @Test
    void delete_notOwnerThrows() {
        Worklog worklog = new Worklog();
        worklog.setWorklogId(5L);
        worklog.setEmployee(employee);
        worklog.setStatus(com.apex.timekeeping.common.enums.ApprovalStatus.PENDING);

        when(worklogRepository.findById(5L)).thenReturn(Optional.of(worklog));

        // Different userId (2L != 1L)
        assertThatThrownBy(() -> worklogService.delete(5L, 2L))
                .isInstanceOf(BusinessException.class);
    }
}
