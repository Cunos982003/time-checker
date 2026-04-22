package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import com.apex.timekeeping.domain.entity.Ot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtRepository extends JpaRepository<Ot, Long> {

    List<Ot> findByEmployee_UserIdOrderByOtDateDesc(Long userId);

    Page<Ot> findByStatus(ApprovalStatus status, Pageable pageable);

    @Query("SELECT o FROM Ot o WHERE o.employee.manager.userId = :managerId AND o.status = 'PENDING'")
    Page<Ot> findPendingForManager(@Param("managerId") Long managerId, Pageable pageable);

    @Query("SELECT SUM(o.workHoursOt) FROM Ot o WHERE o.employee.userId = :userId AND o.status = 'APPROVED' AND o.otDate >= :startDate")
    java.math.BigDecimal sumApprovedHoursByEmployee(@Param("userId") Long userId,
                                                    @Param("startDate") java.time.LocalDate startDate);
}
