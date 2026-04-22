package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployee_UserIdAndLeaveType_LeaveTypeIdAndYear(
            Long userId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByEmployee_UserIdAndYear(Long userId, Integer year);
}
