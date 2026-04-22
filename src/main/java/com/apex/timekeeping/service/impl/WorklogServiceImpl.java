package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.worklog.WorklogRequest;
import com.apex.timekeeping.domain.dto.worklog.WorklogResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.Worklog;
import com.apex.timekeeping.domain.entity.WorklogConfirmation;
import com.apex.timekeeping.domain.repository.*;
import com.apex.timekeeping.service.IWorklogService;
import com.apex.timekeeping.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorklogServiceImpl implements IWorklogService {

    private final WorklogRepository             worklogRepository;
    private final WorklogConfirmationRepository worklogConfirmationRepository;
    private final ProjectRepository             projectRepository;
    private final EmployeeRepository            employeeRepository;
    private final EmailService                  emailService;
    private final ModelMapper                   modelMapper;

    @Override
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
        return toWorklogResponse(worklogRepository.save(wl));
    }

    @Override
    public List<WorklogResponse> getMyWorklogs(Long userId) {
        return worklogRepository.findByEmployee_UserIdOrderByWorkDateDesc(userId)
                .stream().map(this::toWorklogResponse).toList();
    }

    @Override
    @Transactional
    public WorklogResponse update(Long worklogId, Long userId, WorklogRequest req) {
        Worklog wl = getOrThrow(worklogId);
        if (!wl.getEmployee().getUserId().equals(userId))
            throw new BusinessException("Cannot edit another employee's worklog");
        if (wl.getStatus() != ApprovalStatus.PENDING)
            throw new BusinessException("Only pending worklogs can be edited");
        modelMapper.map(req, wl);
        return toWorklogResponse(worklogRepository.save(wl));
    }

    @Override
    @Transactional
    public void delete(Long worklogId, Long userId) {
        Worklog wl = getOrThrow(worklogId);
        if (!wl.getEmployee().getUserId().equals(userId))
            throw new BusinessException("Cannot delete another employee's worklog");
        if (wl.getStatus() != ApprovalStatus.PENDING)
            throw new BusinessException("Only pending worklogs can be deleted");
        worklogRepository.delete(wl);
    }

    @Override
    public Page<WorklogResponse> getPendingForManager(Long managerId, Pageable pageable) {
        return worklogRepository.findPendingForManager(managerId, pageable)
                .map(this::toWorklogResponse);
    }

    @Override
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
        return toWorklogResponse(wl);
    }

    @Override
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
        return toWorklogResponse(wl);
    }

    // ===================== PRIVATE HELPERS =====================

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

    private WorklogResponse toWorklogResponse(Worklog wl) {
        WorklogResponse res = modelMapper.map(wl, WorklogResponse.class);
        res.setStatus(wl.getStatus().name());
        res.setEmployeeName(wl.getEmployee().getFullname());
        res.setProjectName(wl.getProject() != null ? wl.getProject().getProjectName() : null);
        return res;
    }
}
