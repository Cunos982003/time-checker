package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.project.ProjectRequest;
import com.apex.timekeeping.domain.dto.project.ProjectResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.Project;
import com.apex.timekeeping.domain.entity.ProjectAssignment;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import com.apex.timekeeping.domain.repository.ProjectAssignmentRepository;
import com.apex.timekeeping.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;

    public Page<ProjectResponse> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(this::toResponse);
    }

    public ProjectResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public ProjectResponse create(ProjectRequest req) {
        if (projectRepository.existsByProjectCode(req.getProjectCode()))
            throw new BusinessException("Project code already exists");
        Project p = new Project();
        applyRequest(p, req);
        return toResponse(projectRepository.save(p));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest req) {
        Project p = getOrThrow(id);
        applyRequest(p, req);
        return toResponse(projectRepository.save(p));
    }

    @Transactional
    public void assign(Long projectId, Long userId, String role) {
        if (assignmentRepository.existsByProject_ProjectIdAndEmployee_UserId(projectId, userId))
            throw new BusinessException("Employee already assigned to this project");
        Project project = getOrThrow(projectId);
        Employee emp = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        ProjectAssignment pa = new ProjectAssignment();
        pa.setProject(project);
        pa.setEmployee(emp);
        pa.setRoleInProject(role);
        assignmentRepository.save(pa);
    }

    @Transactional
    public void removeAssignment(Long projectId, Long userId) {
        assignmentRepository.deleteByProject_ProjectIdAndEmployee_UserId(projectId, userId);
    }

    private void applyRequest(Project p, ProjectRequest req) {
        p.setProjectCode(req.getProjectCode());
        p.setProjectName(req.getProjectName());
        p.setDescription(req.getDescription());
        p.setStartDate(req.getStartDate());
        p.setEndDate(req.getEndDate());
        if (req.getManagerId() != null)
            p.setManager(employeeRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found")));
    }

    private Project getOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    private ProjectResponse toResponse(Project p) {
        return ProjectResponse.builder()
                .projectId(p.getProjectId())
                .projectCode(p.getProjectCode())
                .projectName(p.getProjectName())
                .description(p.getDescription())
                .status(p.getStatus())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .managerName(p.getManager() != null ? p.getManager().getFullname() : null)
                .managerId(p.getManager() != null ? p.getManager().getUserId() : null)
                .build();
    }
}
