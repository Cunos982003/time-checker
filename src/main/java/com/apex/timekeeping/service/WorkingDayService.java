package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.workingday.WorkingDayRequest;
import com.apex.timekeeping.domain.dto.workingday.WorkingDayResponse;
import com.apex.timekeeping.domain.entity.WorkingDay;
import com.apex.timekeeping.domain.repository.WorkingDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingDayService {

    private final WorkingDayRepository workingDayRepository;

    public List<WorkingDayResponse> findByRange(LocalDate start, LocalDate end) {
        return workingDayRepository.findByDayBetween(start, end)
                .stream().map(this::toResponse).toList();
    }

    public WorkingDayResponse findByDay(LocalDate day) {
        return toResponse(getOrThrow(day));
    }

    @Transactional
    public WorkingDayResponse upsert(WorkingDayRequest req) {
        WorkingDay wd = workingDayRepository.findById(req.getDay())
                .orElse(new WorkingDay());
        wd.setDay(req.getDay());
        wd.setCheckinTime(req.getCheckinTime());
        wd.setCheckoutTime(req.getCheckoutTime());
        wd.setOtRate(req.getOtRate());
        return toResponse(workingDayRepository.save(wd));
    }

    @Transactional
    public void delete(LocalDate day) {
        workingDayRepository.delete(getOrThrow(day));
    }

    private WorkingDay getOrThrow(LocalDate day) {
        return workingDayRepository.findById(day)
                .orElseThrow(() -> new ResourceNotFoundException("Working day not found: " + day));
    }

    private WorkingDayResponse toResponse(WorkingDay wd) {
        return WorkingDayResponse.builder()
                .day(wd.getDay())
                .checkinTime(wd.getCheckinTime())
                .checkoutTime(wd.getCheckoutTime())
                .otRate(wd.getOtRate())
                .build();
    }
}
