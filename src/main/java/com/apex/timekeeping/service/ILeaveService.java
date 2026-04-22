package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.leave.LeaveBalanceResponse;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestDto;
import com.apex.timekeeping.domain.dto.leave.LeaveRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ILeaveService {
    List<LeaveBalanceResponse> getMyBalances(Long userId);
    LeaveRequestResponse create(Long userId, LeaveRequestDto req);
    LeaveRequestResponse cancel(Long requestId, Long userId);
    LeaveRequestResponse approve(Long requestId, Long approverId);
    LeaveRequestResponse reject(Long requestId, Long approverId, String reason);
    List<LeaveRequestResponse> getMyRequests(Long userId);
    Page<LeaveRequestResponse> getPendingForManager(Long managerId, Pageable pageable);
}
