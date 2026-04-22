package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.ot.OtRequest;
import com.apex.timekeeping.domain.dto.ot.OtResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOtService {
    OtResponse create(Long userId, OtRequest req);
    List<OtResponse> getMyOt(Long userId);
    Page<OtResponse> getPendingForManager(Long managerId, Pageable pageable);
    OtResponse approve(Long otId, Long approverId);
    OtResponse reject(Long otId, Long approverId);
    void convertToLeave(Long otId);
}
