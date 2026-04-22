package com.apex.timekeeping.service;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.worklog.WorklogRequest;
import com.apex.timekeeping.domain.dto.worklog.WorklogResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.Worklog;
import com.apex.timekeeping.domain.entity.WorklogConfirmation;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import com.apex.timekeeping.domain.repository.ProjectRepository;
import com.apex.timekeeping.domain.repository.WorklogConfirmationRepository;
import com.apex.timekeeping.domain.repository.WorklogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorklogService {

    private final WorklogRepository worklogRepository;
    private final WorklogConfirmationRepository worklogConfirmationRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @Transactional
    public WorklogResponse create(Long userId, WorklogRequest req) {
        Employee emp = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Worklog wl = new Worklog();
        wl.setEmployee(emp);
        wl.setWorkDate(req.getWorkDate());
        wl.setStartTime(req.getStartTime());
        wl.setEndTime(req.getEndTime());
        wl.setWorkHours(req.getWorkHours());
        wl.setContent(req.getContent());
        wl.setWorkType(req.getWorkType());
        if (req.getProjectId() != null)
            wl.setProject(projectRepository.findById(req.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found")));
        return toResponse(worklogRepository.save(wl));
    }

    public List<WorklogResponse> getMyWorklogs(Long userId) {
        return worklogRepository.findByEmployee_UserIdOrderByWorkDateDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public WorklogResponse update(Long worklogId, Long userId, WorklogRequest req) {
        Worklog wl = getOrThrow(worklogId);
        if (!wl.getEmployee().getUserId().equals(userId))
            throw new BusinessException("Cannot edit another employee's worklog");
        if (wl.getStatus() != ApprovalStatus.PENDING)
            throw new BusinessException("Only pending worklogs can be edited");
        wl.setContent(req.getContent());
        wl.setWorkHours(req.getWorkHours());
        wl.setWorkDate(req.getWorkDate());
        wl.setStartTime(req.getStartTime());
        wl.setEndTime(req.getEndTime());
        return toResponse(worklogRepository.save(wl));
    }

    @Transactional
    public void delete(Long worklogId, Long userId) {
        Worklog wl = getOrThrow(worklogId);
        if (!wl.getEmployee().getUserId().equals(userId))
            throw new BusinessException("Cannot delete another employee's worklog");
        if (wl.getStatus() != ApprovalStatus.PENDING)
            throw new BusinessException("Only pending worklogs can be deleted");
        worklogRepository.delete(wl);
    }

    public Page<WorklogResponse> getPendingForManager(Long managerId, Pageable pageable) {
        return worklogRepository.findPendingForManager(managerId, pageable).map(this::toResponse);
    }

    @Transactional
    public WorklogResponse approve(Long worklogId, Long approverId, String note) {
        Worklog wl = getOrThrow(worklogId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        wl.setStatus(ApprovalStatus.APPROVED);
        worklogRepository.save(wl);
        saveConfirmation(wl, approver, note);
        emailService.sendApprovalResult(wl.getEmployee().getEmail(), "Worklog Approved",
                "Your worklog for " + wl.getWorkDate() + " has been approved.");
        return toResponse(wl);
    }

    @Transactional
    public WorklogResponse reject(Long worklogId, Long approverId, String note) {
        Worklog wl = getOrThrow(worklogId);
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        wl.setStatus(ApprovalStatus.REJECTED);
        worklogRepository.save(wl);
        saveConfirmation(wl, approver, note);
        emailService.sendApprovalResult(wl.getEmployee().getEmail(), "Worklog Rejected",
                "Your worklog for " + wl.getWorkDate() + " has been rejected. Note: " + note);
        return toResponse(wl);
    }

    private void saveConfirmation(Worklog wl, Employee approver, String note) {
        WorklogConfirmation cf = new WorklogConfirmation();
        cf.setWorklog(wl);
        cf.setConfirmedBy(approver);
        cf.setNote(note);
        worklogConfirmationRepository.save(cf);
    }

    private Worklog getOrThrow(Long id) {
        return worklogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worklog not found: " + id));
    }

    private WorklogResponse toResponse(Worklog wl) {
        return WorklogResponse.builder()
                .worklogId(wl.getWorklogId())
                .workDate(wl.getWorkDate())
                .startTime(wl.getStartTime())
                .endTime(wl.getEndTime())
                .workHours(wl.getWorkHours())
                .content(wl.getContent())
                .workType(wl.getWorkType())
                .status(wl.getStatus().name())
                .projectName(wl.getProject() != null ? wl.getProject().getProjectName() : null)
                .employeeName(wl.getEmployee().getFullname())
                .createdAt(wl.getCreatedAt())
                .build();
    }
}
