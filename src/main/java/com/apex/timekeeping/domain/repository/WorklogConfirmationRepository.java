package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.WorklogConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorklogConfirmationRepository extends JpaRepository<WorklogConfirmation, Long> {
}
