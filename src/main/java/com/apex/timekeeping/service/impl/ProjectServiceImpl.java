package com.apex.timekeeping.service.impl;

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
import com.apex.timekeeping.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository           projectRepository;
    private final ProjectAssignmentRepository assignmentRepository;
    private final EmployeeRepository          employeeRepository;
    private final ModelMapper                 modelMapper;

    @Override
    public Page<ProjectResponse> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public ProjectResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest req) {
        if (projectRepository.existsByProjectCode(req.getProjectCode()))
            throw new BusinessException("Project code already exists");
        Project p = new Project();
        applyRequest(p, req);
        return toResponse(projectRepository.save(p));
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest req) {
        Project p = getOrThrow(id);
        applyRequest(p, req);
        return toResponse(projectRepository.save(p));
    }

    @Override
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
        pa.setRole(role);
        assignmentRepository.save(pa);
    }

    @Override
    @Transactional
    public void removeAssignment(Long projectId, Long userId) {
        assignmentRepository.deleteByProject_ProjectIdAndEmployee_UserId(projectId, userId);
    }

    private void applyRequest(Project p, ProjectRequest req) {
        modelMapper.map(req, p);
        if (req.getLeaderId() != null)
            p.setLeader(employeeRepository.findById(req.getLeaderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leader not found")));
    }

    private Project getOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    private ProjectResponse toResponse(Project p) {
        ProjectResponse res = modelMapper.map(p, ProjectResponse.class);
        res.setLeaderId(p.getLeader()   != null ? p.getLeader().getUserId()   : null);
        res.setLeaderName(p.getLeader() != null ? p.getLeader().getFullname() : null);
        return res;
    }
}
