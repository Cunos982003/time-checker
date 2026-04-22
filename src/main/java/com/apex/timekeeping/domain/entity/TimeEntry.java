package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_entries",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","checkin_date"}))
@Getter @Setter @NoArgsConstructor
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_entry_id")
    private Long timeEntryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Employee employee;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(name = "checkin_time")
    private LocalTime checkinTime;

    @Column(name = "checkout_date")
    private LocalDate checkoutDate;

    @Column(name = "checkout_time")
    private LocalTime checkoutTime;

    @Column(name = "number_minutes_late")
    private Integer numberMinutesLate;

    @Column(name = "number_minutes_quit_early")
    private Integer numberMinutesQuitEarly;
}
