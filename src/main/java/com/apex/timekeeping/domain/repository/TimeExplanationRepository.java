package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.domain.entity.TimeExplanation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeExplanationRepository extends JpaRepository<TimeExplanation, Long> {

    @Query("SELECT te FROM TimeExplanation te JOIN FETCH te.timeEntry e WHERE e.employee.userId = :userId ORDER BY te.createDate DESC")
    List<TimeExplanation> findByEmployeeId(@Param("userId") Long userId);

    Page<TimeExplanation> findByStatus(ApprovalStatus status, Pageable pageable);

    @Query("SELECT te FROM TimeExplanation te JOIN te.timeEntry e WHERE e.employee.manager.userId = :managerId AND te.status = 'PENDING'")
    Page<TimeExplanation> findPendingForManager(@Param("managerId") Long managerId, Pageable pageable);
}
