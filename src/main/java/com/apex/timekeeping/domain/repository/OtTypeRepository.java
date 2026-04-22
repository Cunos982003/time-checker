package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.OtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtTypeRepository extends JpaRepository<OtType, Long> {
}
