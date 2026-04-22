package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "annual_leave")
@Getter
@Setter
@NoArgsConstructor
public class AnnualLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "annual_leave_id")
    private Long annualLeaveId;

    @Column(name = "annual_leave_number", nullable = false, precision = 5, scale = 1)
    private BigDecimal annualLeaveNumber = BigDecimal.ZERO;

    @Column(name = "ct_converted_number", nullable = false, precision = 5, scale = 1)
    private BigDecimal ctConvertedNumber = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Employee employee;
}
