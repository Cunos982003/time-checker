package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.domain.entity.Worklog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorklogRepository extends JpaRepository<Worklog, Long> {

    List<Worklog> findByEmployee_UserIdOrderByWorkDateDesc(Long userId);

    Page<Worklog> findByStatus(ApprovalStatus status, Pageable pageable);

    @Query("SELECT w FROM Worklog w WHERE w.employee.manager.userId = :managerId AND w.status = 'PENDING'")
    Page<Worklog> findPendingForManager(@Param("managerId") Long managerId, Pageable pageable);

    @Query("SELECT w FROM Worklog w WHERE w.project.projectId = :projectId AND w.workDate BETWEEN :start AND :end")
    List<Worklog> findByProjectAndDateRange(@Param("projectId") Long projectId,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);
}
