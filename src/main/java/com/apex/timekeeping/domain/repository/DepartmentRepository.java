package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByStatus(String status);

    Optional<Department> findByDepartCode(String departCode);

    boolean existsByDepartCode(String departCode);

    boolean existsByDepartName(String departName);
}
