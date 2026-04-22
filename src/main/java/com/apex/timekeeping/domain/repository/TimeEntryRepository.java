package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.TimeEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    Optional<TimeEntry> findByEmployee_UserIdAndCheckinDate(Long userId, LocalDate date);

    boolean existsByEmployee_UserIdAndCheckinDate(Long userId, LocalDate date);

    List<TimeEntry> findByEmployee_UserIdAndCheckinDateBetweenOrderByCheckinDate(
            Long userId, LocalDate start, LocalDate end);

    @Query("SELECT te FROM TimeEntry te JOIN FETCH te.employee WHERE te.employee.userId = :userId " +
           "AND te.checkinDate BETWEEN :start AND :end ORDER BY te.checkinDate")
    List<TimeEntry> findMonthlyEntries(@Param("userId") Long userId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    Page<TimeEntry> findByCheckinDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    @Query("SELECT te FROM TimeEntry te WHERE te.employee.department.departId = :deptId " +
           "AND te.checkinDate BETWEEN :start AND :end")
    List<TimeEntry> findByDepartmentAndDateRange(@Param("deptId") Long deptId,
                                                  @Param("start") LocalDate start,
                                                  @Param("end") LocalDate end);
}
