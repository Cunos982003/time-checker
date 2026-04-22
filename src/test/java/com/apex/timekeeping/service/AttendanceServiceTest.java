package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.domain.dto.attendance.AttendanceResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.TimeEntry;
import com.apex.timekeeping.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock private TimeEntryRepository timeEntryRepository;
    @Mock private WorkingDayRepository workingDayRepository;
    @Mock private TimeExplanationRepository timeExplanationRepository;
    @Mock private ConfirmExplanationRepository confirmExplanationRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private AttendanceService attendanceService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setUserId(1L);
        employee.setFullname("Test User");
        employee.setEmail("test@example.com");
    }

    @Test
    void checkIn_success() {
        when(timeEntryRepository.existsByEmployee_UserIdAndCheckinDate(1L, LocalDate.now())).thenReturn(false);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(workingDayRepository.findById(LocalDate.now())).thenReturn(Optional.empty());

        TimeEntry saved = new TimeEntry();
        saved.setTimeEntryId(10L);
        saved.setEmployee(employee);
        saved.setCheckinDate(LocalDate.now());
        saved.setCheckinTime(LocalTime.of(8, 30));
        when(timeEntryRepository.save(any())).thenReturn(saved);

        AttendanceResponse response = attendanceService.checkIn(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTimeEntryId()).isEqualTo(10L);
        verify(timeEntryRepository).save(any(TimeEntry.class));
    }

    @Test
    void checkIn_duplicateThrows() {
        when(timeEntryRepository.existsByEmployee_UserIdAndCheckinDate(1L, LocalDate.now())).thenReturn(true);

        assertThatThrownBy(() -> attendanceService.checkIn(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Already checked in today");
    }

    @Test
    void checkOut_success() {
        TimeEntry existing = new TimeEntry();
        existing.setTimeEntryId(10L);
        existing.setEmployee(employee);
        existing.setCheckinDate(LocalDate.now());
        existing.setCheckinTime(LocalTime.of(8, 30));

        when(timeEntryRepository.findByEmployee_UserIdAndCheckinDate(1L, LocalDate.now()))
                .thenReturn(Optional.of(existing));
        when(workingDayRepository.findById(LocalDate.now())).thenReturn(Optional.empty());
        when(timeEntryRepository.save(any())).thenReturn(existing);

        AttendanceResponse response = attendanceService.checkOut(1L);

        assertThat(response).isNotNull();
        verify(timeEntryRepository).save(existing);
    }

    @Test
    void checkOut_noCheckinThrows() {
        when(timeEntryRepository.findByEmployee_UserIdAndCheckinDate(1L, LocalDate.now()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkOut(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No check-in record found");
    }

    @Test
    void getTodayStatus_absent_returnsAbsentResponse() {
        when(timeEntryRepository.findByEmployee_UserIdAndCheckinDate(1L, LocalDate.now()))
                .thenReturn(Optional.empty());

        AttendanceResponse response = attendanceService.getTodayStatus(1L);

        assertThat(response.getStatus()).isEqualTo("ABSENT");
    }
}
