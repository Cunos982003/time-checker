package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.domain.dto.position.PositionRequest;
import com.apex.timekeeping.domain.dto.position.PositionResponse;
import com.apex.timekeeping.domain.entity.Position;
import com.apex.timekeeping.domain.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    public List<PositionResponse> findAll() {
        return positionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public PositionResponse create(PositionRequest req) {
        if (positionRepository.existsByJobCode(req.getJobCode()))
            throw new BusinessException("Job code already exists");
        Position p = new Position();
        p.setJobCode(req.getJobCode());
        p.setJobName(req.getJobName());
        p.setLevel(req.getLevel());
        p.setJobDescription(req.getJobDescription());
        return toResponse(positionRepository.save(p));
    }

    private PositionResponse toResponse(Position p) {
        return PositionResponse.builder()
                .jobId(p.getJobId())
                .jobCode(p.getJobCode())
                .jobName(p.getJobName())
                .level(p.getLevel())
                .jobDescription(p.getJobDescription())
                .build();
    }
}
