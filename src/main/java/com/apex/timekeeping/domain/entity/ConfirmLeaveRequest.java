package com.apex.timekeeping.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "confirm_leave_request")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmLeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cf_request_id")
    private Long cfRequestId;

    @Column(name = "confirm_result", columnDefinition = "TEXT")
    private String confirmResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approve_id")
    private Employee approver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private LeaveRequest leaveRequest;
}
