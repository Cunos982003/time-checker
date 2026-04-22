package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.project.ProjectRequest;
import com.apex.timekeeping.domain.dto.project.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProjectService {
    Page<ProjectResponse> findAll(Pageable pageable);
    ProjectResponse findById(Long id);
    ProjectResponse create(ProjectRequest req);
    ProjectResponse update(Long id, ProjectRequest req);
    void assign(Long projectId, Long userId, String role);
    void removeAssignment(Long projectId, Long userId);
}
