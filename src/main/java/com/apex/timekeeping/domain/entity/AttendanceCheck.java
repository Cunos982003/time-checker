package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "attendance_checks")
@Getter
@Setter
@NoArgsConstructor
public class AttendanceCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_id")
    private Long checkId;

    @Column(name = "a_date", nullable = false)
    private LocalDate aDate;

    @Column(name = "ct_quit_early")
    private final Integer ctQuitEarly = 0;

    @Column(name = "ct_checkin_late")
    private final Integer ctCheckinLate = 0;

    @Column(name = "confirm_date")
    private LocalDate confirmDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_entry_id", nullable = false, unique = true)
    private TimeEntry timeEntry;
}
