package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Long> {

    List<PublicHoliday> findByHolidayDateBetween(LocalDate start, LocalDate end);

    boolean existsByHolidayDate(LocalDate date);
}
