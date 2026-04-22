package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByJobCode(String jobCode);

    boolean existsByJobCode(String jobCode);
}
