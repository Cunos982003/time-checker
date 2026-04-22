package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "worklog_confirmations")
@Getter
@Setter
@NoArgsConstructor
public class WorklogConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirmation_id")
    private Long confirmationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worklog_id", unique = true, nullable = false)
    private Worklog worklog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by", nullable = false)
    private Employee confirmedBy;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "confirmed_at", updatable = false)
    private LocalDateTime confirmedAt;

    @PrePersist
    public void prePersist() {
        confirmedAt = LocalDateTime.now();
    }
}
