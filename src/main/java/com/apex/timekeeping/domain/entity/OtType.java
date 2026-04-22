package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ot_types")
@Getter @Setter @NoArgsConstructor
public class OtType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ot_rate", precision = 5, scale = 2)
    private BigDecimal otRate;
}
