package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.domain.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee_UserIdOrderByCreatedAtDesc(Long userId);

    Page<LeaveRequest> findByStatus(ApprovalStatus status, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.manager.userId = :managerId AND lr.status = 'PENDING'")
    Page<LeaveRequest> findPendingForManager(@Param("managerId") Long managerId, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.userId = :userId " +
           "AND lr.status NOT IN ('REJECTED','CANCELLED') " +
           "AND NOT (lr.endDate < :start OR lr.startDate > :end)")
    List<LeaveRequest> findOverlapping(@Param("userId") Long userId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);
}
