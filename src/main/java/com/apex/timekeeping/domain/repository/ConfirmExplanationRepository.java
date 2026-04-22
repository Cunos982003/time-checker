package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.ConfirmExplanation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmExplanationRepository extends JpaRepository<ConfirmExplanation, Long> {
}
