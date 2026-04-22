package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.position.PositionRequest;
import com.apex.timekeeping.domain.dto.position.PositionResponse;
import com.apex.timekeeping.domain.entity.Position;
import com.apex.timekeeping.domain.repository.PositionRepository;
import com.apex.timekeeping.service.IPositionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements IPositionService {

    private final PositionRepository positionRepository;
    private final ModelMapper        modelMapper;

    @Override
    public List<PositionResponse> findAll() {
        return positionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public PositionResponse create(PositionRequest req) {
        if (positionRepository.existsByJobCode(req.getJobCode()))
            throw new BusinessException("Job code already exists");
        Position p = new Position();
        modelMapper.map(req, p);
        return toResponse(positionRepository.save(p));
    }

    @Override
    @Transactional
    public PositionResponse update(Long id, PositionRequest req) {
        Position p = getOrThrow(id);
        modelMapper.map(req, p);
        return toResponse(positionRepository.save(p));
    }

    private Position getOrThrow(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + id));
    }

    private PositionResponse toResponse(Position p) {
        return modelMapper.map(p, PositionResponse.class);
    }
}
