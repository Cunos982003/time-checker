package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "confirm_ot")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmOt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cf_ot_id")
    private Long cfOtId;

    @Column(name = "cf_date")
    private LocalDate cfDate;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approve_id")
    private Employee approver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ot_id", nullable = false, unique = true)
    private Ot ot;
}
