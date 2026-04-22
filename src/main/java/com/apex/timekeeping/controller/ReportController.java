package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.domain.dto.report.DailyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.MonthlyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.WorkSummaryReport;
import com.apex.timekeeping.service.ExcelExportService;
import com.apex.timekeeping.service.IReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ReportController {

    private final IReportService reportService;
    private final ExcelExportService excelExportService;

    // --- Daily attendance ---
    @GetMapping("/attendance/daily")
    public ResponseEntity<ApiResponse<List<DailyAttendanceReport>>> dailyAttendance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long departId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getDailyAttendance(date, departId)));
    }

    @GetMapping("/attendance/daily/export")
    public ResponseEntity<byte[]> exportDailyAttendance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long departId) {
        List<DailyAttendanceReport> data = reportService.getDailyAttendance(date, departId);
        byte[] bytes = excelExportService.exportDailyAttendance(data);
        return excelResponse(bytes, "daily-attendance-" + date + ".xlsx");
    }

    // --- Monthly attendance ---
    @GetMapping("/attendance/monthly")
    public ResponseEntity<ApiResponse<List<MonthlyAttendanceReport>>> monthlyAttendance(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Long departId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getMonthlyAttendance(year, month, departId)));
    }

    @GetMapping("/attendance/monthly/export")
    public ResponseEntity<byte[]> exportMonthlyAttendance(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Long departId) {
        List<MonthlyAttendanceReport> data = reportService.getMonthlyAttendance(year, month, departId);
        byte[] bytes = excelExportService.exportMonthlyAttendance(data);
        return excelResponse(bytes, "monthly-attendance-" + year + "-" + month + ".xlsx");
    }

    // --- Work by department ---
    @GetMapping("/work/by-department")
    public ResponseEntity<ApiResponse<List<WorkSummaryReport>>> workByDepartment(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getWorkByDepartment(start, end)));
    }

    @GetMapping("/work/by-department/export")
    public ResponseEntity<byte[]> exportWorkByDepartment(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        byte[] bytes = excelExportService.exportWorkSummary(reportService.getWorkByDepartment(start, end), "By Department");
        return excelResponse(bytes, "work-by-department.xlsx");
    }

    // --- Work by project ---
    @GetMapping("/work/by-project")
    public ResponseEntity<ApiResponse<List<WorkSummaryReport>>> workByProject(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getWorkByProject(start, end)));
    }

    @GetMapping("/work/by-project/export")
    public ResponseEntity<byte[]> exportWorkByProject(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        byte[] bytes = excelExportService.exportWorkSummary(reportService.getWorkByProject(start, end), "By Project");
        return excelResponse(bytes, "work-by-project.xlsx");
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
