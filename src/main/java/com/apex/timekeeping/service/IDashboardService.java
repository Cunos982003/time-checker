package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.dashboard.DashboardResponse;

public interface IDashboardService {
    DashboardResponse getDashboard(Long userId);
}
