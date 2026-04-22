package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.WorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkingDayRepository extends JpaRepository<WorkingDay, LocalDate> {

    List<WorkingDay> findByDayBetween(LocalDate start, LocalDate end);

    boolean existsByDay(LocalDate day);
}
