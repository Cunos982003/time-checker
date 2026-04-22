package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.worklog.WorklogRequest;
import com.apex.timekeeping.domain.dto.worklog.WorklogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IWorklogService {
    WorklogResponse create(Long userId, WorklogRequest req);
    List<WorklogResponse> getMyWorklogs(Long userId);
    WorklogResponse update(Long worklogId, Long userId, WorklogRequest req);
    void delete(Long worklogId, Long userId);
    Page<WorklogResponse> getPendingForManager(Long managerId, Pageable pageable);
    WorklogResponse approve(Long worklogId, Long approverId, String note);
    WorklogResponse reject(Long worklogId, Long approverId, String note);
}
