package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_types")
@Getter
@Setter
@NoArgsConstructor
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_type_id")
    private Long leaveTypeId;

    @Column(name = "type_code", unique = true, nullable = false, length = 30)
    private String typeCode;

    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    @Column(name = "max_days_per_year", precision = 5, scale = 1)
    private BigDecimal maxDaysPerYear;

    @Column(name = "is_paid")
    private final Boolean isPaid = true;

    @Column(name = "requires_approval")
    private final Boolean requiresApproval = true;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active")
    private final Boolean isActive = true;
}
