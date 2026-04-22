package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "working_days")
@Getter
@Setter
@NoArgsConstructor
public class WorkingDay {

    @Id
    @Column(name = "day")
    private java.time.LocalDate day;

    @Column(name = "checkin_time")
    private java.time.LocalTime checkinTime;

    @Column(name = "checkout_time")
    private java.time.LocalTime checkoutTime;

    @Column(name = "ot_rate", precision = 5, scale = 2)
    private java.math.BigDecimal otRate;
}
