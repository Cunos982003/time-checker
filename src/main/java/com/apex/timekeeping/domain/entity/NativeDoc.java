package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "native_doc")
@Getter
@Setter
@NoArgsConstructor
public class NativeDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_id")
    private Long uploadId;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "title", length = 300)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_by")
    private Employee uploadBy;
}
