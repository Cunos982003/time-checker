package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    List<LeaveType> findByIsActiveTrue();

    Optional<LeaveType> findByTypeCode(String typeCode);
}
