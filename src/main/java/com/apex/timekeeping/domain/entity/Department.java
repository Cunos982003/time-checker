package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depart_id")
    private Long departId;

    @Column(name = "depart_code", unique = true, nullable = false, length = 20)
    private String departCode;

    @Column(name = "depart_name", nullable = false, length = 100)
    private String departName;

    @Column(name = "depart_description", length = 500)
    private String departDescription;

    @Column(name = "status", length = 20)
    private final String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;
}
