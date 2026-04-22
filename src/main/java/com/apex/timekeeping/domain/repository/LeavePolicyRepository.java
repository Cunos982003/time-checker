package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.LeavePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {

    List<LeavePolicy> findByDepartment_DepartIdAndEffectiveYear(Long departId, Integer year);

    Optional<LeavePolicy> findByDepartment_DepartIdAndLeaveType_LeaveTypeIdAndEffectiveYear(
            Long departId, Long leaveTypeId, Integer year);
}
