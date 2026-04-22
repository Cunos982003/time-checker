package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "confirm_explanations")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmExplanation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cf_explain_id")
    private Long cfExplainId;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "cf_date")
    private LocalDate cfDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id")
    private Employee approver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "explain_id", nullable = false, unique = true)
    private TimeExplanation timeExplanation;
}
