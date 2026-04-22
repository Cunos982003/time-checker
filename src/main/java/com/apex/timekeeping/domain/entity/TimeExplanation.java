package com.apex.timekeeping.domain.entity;

import com.apex.timekeeping.common.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "time_explanations")
@Getter @Setter @NoArgsConstructor
public class TimeExplanation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "explain_id")
    private Long explainId;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "picture", length = 500)
    private String picture;    // file path or URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_entry_id", nullable = false)
    private TimeEntry timeEntry;

    @OneToOne(mappedBy = "timeExplanation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ConfirmExplanation confirmExplanation;
}
