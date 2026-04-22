package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.domain.dto.report.DailyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.MonthlyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.WorkSummaryReport;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.LeaveRequest;
import com.apex.timekeeping.domain.entity.TimeEntry;
import com.apex.timekeeping.domain.entity.Worklog;
import com.apex.timekeeping.domain.repository.*;
import com.apex.timekeeping.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

    private final TimeEntryRepository    timeEntryRepository;
    private final WorklogRepository      worklogRepository;
    private final EmployeeRepository     employeeRepository;
    private final OtRepository           otRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public List<DailyAttendanceReport> getDailyAttendance(LocalDate date, Long departId) {
        List<TimeEntry> entries = (departId != null)
                ? timeEntryRepository.findByDepartmentAndDateRange(departId, date, date)
                : timeEntryRepository.findByCheckinDateBetween(date, date, Pageable.unpaged()).getContent();

        return entries.stream().map(te -> {
            String status = "PRESENT";
            if (te.getNumberMinutesLate()      != null && te.getNumberMinutesLate()      > 0) status = "LATE";
            if (te.getNumberMinutesQuitEarly() != null && te.getNumberMinutesQuitEarly() > 0) status = "EARLY_QUIT";
            Employee emp = te.getEmployee();
            return DailyAttendanceReport.builder()
                    .userId(emp.getUserId())
                    .empId(emp.getEmpId())
                    .fullName(emp.getFullname())
                    .department(emp.getDepartment() != null ? emp.getDepartment().getDepartName() : null)
                    .date(te.getCheckinDate())
                    .checkinTime(te.getCheckinTime())
                    .checkoutTime(te.getCheckoutTime())
                    .minutesLate(te.getNumberMinutesLate())
                    .minutesEarly(te.getNumberMinutesQuitEarly())
                    .status(status)
                    .build();
        }).toList();
    }

    @Override
    public List<MonthlyAttendanceReport> getMonthlyAttendance(int year, int month, Long departId) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());

        List<TimeEntry> entries = (departId != null)
                ? timeEntryRepository.findByDepartmentAndDateRange(departId, start, end)
                : timeEntryRepository.findByCheckinDateBetween(start, end, Pageable.unpaged()).getContent();

        Map<Long, List<TimeEntry>> byUser = entries.stream()
                .collect(Collectors.groupingBy(te -> te.getEmployee().getUserId()));

        return byUser.entrySet().stream().map(e -> {
            Long userId = e.getKey();
            List<TimeEntry> ue = e.getValue();
            Employee emp = ue.get(0).getEmployee();

            int late      = (int) ue.stream().filter(te -> te.getNumberMinutesLate()      != null && te.getNumberMinutesLate()      > 0).count();
            int earlyQuit = (int) ue.stream().filter(te -> te.getNumberMinutesQuitEarly() != null && te.getNumberMinutesQuitEarly() > 0).count();

            BigDecimal otHours = otRepository.sumApprovedHoursByEmployee(userId, start);

            BigDecimal leaveDays = leaveRequestRepository
                    .findByEmployee_UserIdOrderByCreatedAtDesc(userId).stream()
                    .filter(lr -> !lr.getStatus().name().equals("REJECTED")
                            && !lr.getStatus().name().equals("CANCELLED")
                            && !lr.getStartDate().isAfter(end)
                            && !lr.getEndDate().isBefore(start))
                    .map(LeaveRequest::getTotalDays)
                    .filter(d -> d != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return MonthlyAttendanceReport.builder()
                    .userId(userId).empId(emp.getEmpId()).fullName(emp.getFullname())
                    .department(emp.getDepartment() != null ? emp.getDepartment().getDepartName() : null)
                    .year(year).month(month)
                    .totalPresent(ue.size()).totalLate(late).totalEarlyQuit(earlyQuit)
                    .totalLeaveDays(leaveDays)
                    .totalOtHours(otHours != null ? otHours : BigDecimal.ZERO)
                    .build();
        }).toList();
    }

    @Override
    public List<WorkSummaryReport> getWorkByDepartment(LocalDate start, LocalDate end) {
        Map<Long, List<Employee>> byDept = employeeRepository.findAll().stream()
                .filter(emp -> emp.getDepartment() != null)
                .collect(Collectors.groupingBy(emp -> emp.getDepartment().getDepartId()));

        return byDept.entrySet().stream().map(e -> {
            List<Employee> members = e.getValue();
            String deptName = members.get(0).getDepartment().getDepartName();
            List<Worklog> logs = members.stream()
                    .flatMap(emp -> worklogRepository.findByEmployee_UserIdOrderByWorkDateDesc(emp.getUserId())
                            .stream().filter(w -> !w.getWorkDate().isBefore(start) && !w.getWorkDate().isAfter(end)))
                    .toList();
            BigDecimal totalHours = logs.stream().map(Worklog::getWorkHours).filter(h -> h != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return WorkSummaryReport.builder()
                    .groupId(e.getKey()).groupName(deptName)
                    .totalMembers(members.size()).totalWorklogs(logs.size()).totalHours(totalHours)
                    .pendingWorklogs((int) logs.stream().filter(w -> "PENDING".equals(w.getStatus().name())).count())
                    .approvedWorklogs((int) logs.stream().filter(w -> "APPROVED".equals(w.getStatus().name())).count())
                    .build();
        }).toList();
    }

    @Override
    public List<WorkSummaryReport> getWorkByProject(LocalDate start, LocalDate end) {
        Map<Long, List<Worklog>> byProject = worklogRepository.findAll().stream()
                .filter(w -> w.getProject() != null
                        && !w.getWorkDate().isBefore(start) && !w.getWorkDate().isAfter(end))
                .collect(Collectors.groupingBy(w -> w.getProject().getProjectId()));

        return byProject.entrySet().stream().map(e -> {
            List<Worklog> logs = e.getValue();
            String projectName = logs.get(0).getProject().getProjectName();
            BigDecimal totalHours = logs.stream().map(Worklog::getWorkHours).filter(h -> h != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long uniqueMembers = logs.stream().map(w -> w.getEmployee().getUserId()).distinct().count();
            return WorkSummaryReport.builder()
                    .groupId(e.getKey()).groupName(projectName)
                    .totalMembers((int) uniqueMembers).totalWorklogs(logs.size()).totalHours(totalHours)
                    .pendingWorklogs((int) logs.stream().filter(w -> "PENDING".equals(w.getStatus().name())).count())
                    .approvedWorklogs((int) logs.stream().filter(w -> "APPROVED".equals(w.getStatus().name())).count())
                    .build();
        }).toList();
    }
}
