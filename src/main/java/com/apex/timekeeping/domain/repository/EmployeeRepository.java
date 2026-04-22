package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmpId(String empId);

    boolean existsByEmail(String email);

    boolean existsByEmpId(String empId);

    Page<Employee> findByStatusAndDepartment_DepartId(String status, Long departId, Pageable pageable);

    Page<Employee> findByStatus(String status, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.manager.userId = :managerId AND e.status = 'ACTIVE'")
    List<Employee> findActiveSubordinates(@Param("managerId") Long managerId);

    @Query("SELECT e FROM Employee e JOIN FETCH e.role JOIN FETCH e.department WHERE e.userId = :id")
    Optional<Employee> findByIdWithDetails(@Param("id") Long id);
}
