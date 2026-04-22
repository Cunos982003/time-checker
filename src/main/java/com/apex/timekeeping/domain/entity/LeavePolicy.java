package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_policies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"depart_id", "leave_type_id", "effective_year"}))
@Getter
@Setter
@NoArgsConstructor
public class LeavePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "entitled_days", nullable = false, precision = 5, scale = 1)
    private BigDecimal entitledDays;

    @Column(name = "carry_over_days", precision = 5, scale = 1)
    private final BigDecimal carryOverDays = BigDecimal.ZERO;

    @Column(name = "effective_year", nullable = false)
    private Integer effectiveYear;
}
