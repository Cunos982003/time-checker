package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectCode(String projectCode);

    boolean existsByProjectCode(String projectCode);

    Page<Project> findByStatus(String status, Pageable pageable);

    Page<Project> findByLeader_UserId(Long leaderId, Pageable pageable);
}
