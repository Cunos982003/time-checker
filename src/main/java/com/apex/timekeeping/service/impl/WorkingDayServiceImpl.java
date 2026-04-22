package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.workingday.WorkingDayRequest;
import com.apex.timekeeping.domain.dto.workingday.WorkingDayResponse;
import com.apex.timekeeping.domain.entity.WorkingDay;
import com.apex.timekeeping.domain.repository.WorkingDayRepository;
import com.apex.timekeeping.service.IWorkingDayService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingDayServiceImpl implements IWorkingDayService {

    private final WorkingDayRepository workingDayRepository;
    private final ModelMapper          modelMapper;

    @Override
    public List<WorkingDayResponse> findByRange(LocalDate start, LocalDate end) {
        return workingDayRepository.findByDayBetween(start, end)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public WorkingDayResponse findByDay(LocalDate day) {
        return toResponse(getOrThrow(day));
    }

    @Override
    @Transactional
    public WorkingDayResponse upsert(WorkingDayRequest req) {
        WorkingDay wd = workingDayRepository.findById(req.getDay()).orElse(new WorkingDay());
        modelMapper.map(req, wd);
        return toResponse(workingDayRepository.save(wd));
    }

    @Override
    @Transactional
    public void delete(LocalDate day) {
        workingDayRepository.delete(getOrThrow(day));
    }

    private WorkingDay getOrThrow(LocalDate day) {
        return workingDayRepository.findById(day)
                .orElseThrow(() -> new ResourceNotFoundException("Working day not found: " + day));
    }

    private WorkingDayResponse toResponse(WorkingDay wd) {
        return modelMapper.map(wd, WorkingDayResponse.class);
    }
}
