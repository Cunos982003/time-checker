package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.workingday.WorkingDayRequest;
import com.apex.timekeeping.domain.dto.workingday.WorkingDayResponse;

import java.time.LocalDate;
import java.util.List;

public interface IWorkingDayService {
    List<WorkingDayResponse> findByRange(LocalDate start, LocalDate end);
    WorkingDayResponse findByDay(LocalDate day);
    WorkingDayResponse upsert(WorkingDayRequest req);
    void delete(LocalDate day);
}
