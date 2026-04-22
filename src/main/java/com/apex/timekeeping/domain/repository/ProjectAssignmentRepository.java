package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

    List<ProjectAssignment> findByProject_ProjectId(Long projectId);

    List<ProjectAssignment> findByEmployee_UserId(Long userId);

    Optional<ProjectAssignment> findByProject_ProjectIdAndEmployee_UserId(Long projectId, Long userId);

    boolean existsByProject_ProjectIdAndEmployee_UserId(Long projectId, Long userId);

    void deleteByProject_ProjectIdAndEmployee_UserId(Long projectId, Long userId);
}
