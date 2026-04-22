package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.report.DailyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.MonthlyAttendanceReport;
import com.apex.timekeeping.domain.dto.report.WorkSummaryReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ExcelExportService {

    public byte[] exportDailyAttendance(List<DailyAttendanceReport> rows) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Daily Attendance");
            CellStyle headerStyle = createHeaderStyle(wb);

            String[] headers = {"Emp ID", "Full Name", "Department", "Date",
                    "Check-in", "Check-out", "Late (min)", "Early Quit (min)", "Status"};
            createHeaderRow(sheet, headers, headerStyle);

            int rowNum = 1;
            for (DailyAttendanceReport r : rows) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(nullSafe(r.getEmpId()));
                row.createCell(1).setCellValue(nullSafe(r.getFullName()));
                row.createCell(2).setCellValue(nullSafe(r.getDepartment()));
                row.createCell(3).setCellValue(r.getDate() != null ? r.getDate().toString() : "");
                row.createCell(4).setCellValue(r.getCheckinTime() != null ? r.getCheckinTime().toString() : "");
                row.createCell(5).setCellValue(r.getCheckoutTime() != null ? r.getCheckoutTime().toString() : "");
                row.createCell(6).setCellValue(r.getMinutesLate() != null ? r.getMinutesLate() : 0);
                row.createCell(7).setCellValue(r.getMinutesEarly() != null ? r.getMinutesEarly() : 0);
                row.createCell(8).setCellValue(nullSafe(r.getStatus()));
            }
            autoSizeColumns(sheet, headers.length);
            return toBytes(wb);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate daily attendance Excel", e);
        }
    }

    public byte[] exportMonthlyAttendance(List<MonthlyAttendanceReport> rows) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Monthly Attendance");
            CellStyle headerStyle = createHeaderStyle(wb);

            String[] headers = {"Emp ID", "Full Name", "Department", "Year", "Month",
                    "Present", "Late", "Early Quit", "Leave Days", "OT Hours"};
            createHeaderRow(sheet, headers, headerStyle);

            int rowNum = 1;
            for (MonthlyAttendanceReport r : rows) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(nullSafe(r.getEmpId()));
                row.createCell(1).setCellValue(nullSafe(r.getFullName()));
                row.createCell(2).setCellValue(nullSafe(r.getDepartment()));
                row.createCell(3).setCellValue(r.getYear() != null ? r.getYear() : 0);
                row.createCell(4).setCellValue(r.getMonth() != null ? r.getMonth() : 0);
                row.createCell(5).setCellValue(r.getTotalPresent() != null ? r.getTotalPresent() : 0);
                row.createCell(6).setCellValue(r.getTotalLate() != null ? r.getTotalLate() : 0);
                row.createCell(7).setCellValue(r.getTotalEarlyQuit() != null ? r.getTotalEarlyQuit() : 0);
                row.createCell(8).setCellValue(r.getTotalLeaveDays() != null ? r.getTotalLeaveDays().doubleValue() : 0);
                row.createCell(9).setCellValue(r.getTotalOtHours() != null ? r.getTotalOtHours().doubleValue() : 0);
            }
            autoSizeColumns(sheet, headers.length);
            return toBytes(wb);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate monthly attendance Excel", e);
        }
    }

    public byte[] exportWorkSummary(List<WorkSummaryReport> rows, String sheetName) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(sheetName);
            CellStyle headerStyle = createHeaderStyle(wb);

            String[] headers = {"Group", "Members", "Total Worklogs", "Total Hours", "Pending", "Approved"};
            createHeaderRow(sheet, headers, headerStyle);

            int rowNum = 1;
            for (WorkSummaryReport r : rows) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(nullSafe(r.getGroupName()));
                row.createCell(1).setCellValue(r.getTotalMembers() != null ? r.getTotalMembers() : 0);
                row.createCell(2).setCellValue(r.getTotalWorklogs() != null ? r.getTotalWorklogs() : 0);
                row.createCell(3).setCellValue(r.getTotalHours() != null ? r.getTotalHours().doubleValue() : 0);
                row.createCell(4).setCellValue(r.getPendingWorklogs() != null ? r.getPendingWorklogs() : 0);
                row.createCell(5).setCellValue(r.getApprovedWorklogs() != null ? r.getApprovedWorklogs() : 0);
            }
            autoSizeColumns(sheet, headers.length);
            return toBytes(wb);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate work summary Excel", e);
        }
    }

    // --- helpers ---
    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private void createHeaderRow(Sheet sheet, String[] headers, CellStyle style) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void autoSizeColumns(Sheet sheet, int count) {
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] toBytes(Workbook wb) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }

    private String nullSafe(String s) {
        return s != null ? s : "";
    }
}
