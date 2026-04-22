package com.apex.timekeeping.controller;

import com.apex.timekeeping.common.ApiResponse;
import com.apex.timekeeping.common.PagedResponse;
import com.apex.timekeeping.domain.dto.department.DepartmentRequest;
import com.apex.timekeeping.domain.dto.department.DepartmentResponse;
import com.apex.timekeeping.domain.dto.employee.CreateEmployeeRequest;
import com.apex.timekeeping.domain.dto.employee.EmployeeResponse;
import com.apex.timekeeping.domain.dto.employee.UpdateEmployeeRequest;
import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeRequest;
import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeResponse;
import com.apex.timekeeping.domain.dto.position.PositionRequest;
import com.apex.timekeeping.domain.dto.position.PositionResponse;
import com.apex.timekeeping.domain.dto.project.ProjectRequest;
import com.apex.timekeeping.domain.dto.project.ProjectResponse;
import com.apex.timekeeping.domain.dto.workingday.WorkingDayRequest;
import com.apex.timekeeping.domain.dto.workingday.WorkingDayResponse;
import com.apex.timekeeping.service.IDepartmentService;
import com.apex.timekeeping.service.IPositionService;
import com.apex.timekeeping.service.IEmployeeService;
import com.apex.timekeeping.service.IProjectService;
import com.apex.timekeeping.service.IWorkingDayService;
import com.apex.timekeeping.service.ILeaveTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Tag(name = "Configuration (Admin)")
public class ConfigController {

    private final IDepartmentService departmentService;
    private final IPositionService positionService;
    private final IEmployeeService employeeService;
    private final IProjectService projectService;
    private final IWorkingDayService workingDayService;
    private final ILeaveTypeService leaveTypeService;

    // --- Departments ---
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> listDepts() {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.findAll()));
    }

    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDept(@Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.create(req)));
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDept(@PathVariable Long id, @Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.update(id, req)));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDept(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }

    // --- Positions ---
    @GetMapping("/positions")
    public ResponseEntity<ApiResponse<List<PositionResponse>>> listPositions() {
        return ResponseEntity.ok(ApiResponse.ok(positionService.findAll()));
    }

    @PostMapping("/positions")
    public ResponseEntity<ApiResponse<PositionResponse>> createPosition(@Valid @RequestBody PositionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(positionService.create(req)));
    }

    // --- Employees ---
    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(employeeService.findAll(page, size))));
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.create(req)));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.update(id, req)));
    }

    // --- Projects ---
    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectResponse>>> listProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(projectService.findAll(PageRequest.of(page, size)))));
    }

    @PostMapping("/projects")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody ProjectRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.create(req)));
    }

    @PostMapping("/projects/{projectId}/assignments")
    public ResponseEntity<ApiResponse<Void>> assign(
            @PathVariable Long projectId,
            @RequestParam Long userId,
            @RequestParam(required = false) String role) {
        projectService.assign(projectId, userId, role);
        return ResponseEntity.ok(ApiResponse.ok("Assigned", null));
    }

    @DeleteMapping("/projects/{projectId}/assignments/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeAssignment(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeAssignment(projectId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Removed", null));
    }

    // --- Working Days ---
    @GetMapping("/working-days")
    public ResponseEntity<ApiResponse<List<WorkingDayResponse>>> listWorkingDays(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.ok(workingDayService.findByRange(start, end)));
    }

    @PutMapping("/working-days")
    public ResponseEntity<ApiResponse<WorkingDayResponse>> upsertWorkingDay(@Valid @RequestBody WorkingDayRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(workingDayService.upsert(req)));
    }

    @DeleteMapping("/working-days/{day}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkingDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        workingDayService.delete(day);
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }

    // --- Leave Types ---
    @GetMapping("/leave-types")
    public ResponseEntity<ApiResponse<List<LeaveTypeResponse>>> listLeaveTypes() {
        return ResponseEntity.ok(ApiResponse.ok(leaveTypeService.findAll()));
    }

    @PostMapping("/leave-types")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> createLeaveType(@Valid @RequestBody LeaveTypeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(leaveTypeService.create(req)));
    }

    @PutMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> updateLeaveType(@PathVariable Long id, @Valid @RequestBody LeaveTypeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(leaveTypeService.update(id, req)));
    }

    @DeleteMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLeaveType(@PathVariable Long id) {
        leaveTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Deactivated", null));
    }
}
