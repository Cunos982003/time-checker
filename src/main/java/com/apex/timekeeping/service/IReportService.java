package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.report.DailyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.MonthlyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.WorkSummaryReport;

import java.time.LocalDate;
import java.util.List;

public interface IReportService {
    List<DailyAttendanceReport> getDailyAttendance(LocalDate date, Long departId);
    List<MonthlyAttendanceReport> getMonthlyAttendance(int year, int month, Long departId);
    List<WorkSummaryReport> getWorkByDepartment(LocalDate start, LocalDate end);
    List<WorkSummaryReport> getWorkByProject(LocalDate start, LocalDate end);
}
