package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "positions")
@Getter @Setter @NoArgsConstructor
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "job_code", nullable = false, unique = true, length = 20)
    private String jobCode;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "level", length = 50)
    private String level;

    @Column(name = "job_description", length = 500)
    private String jobDescription;
}
