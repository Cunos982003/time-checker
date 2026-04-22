package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeRequest;
import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeResponse;

import java.util.List;

public interface ILeaveTypeService {
    List<LeaveTypeResponse> findAll();
    List<LeaveTypeResponse> findActive();
    LeaveTypeResponse create(LeaveTypeRequest req);
    LeaveTypeResponse update(Long id, LeaveTypeRequest req);
    void delete(Long id);
}
