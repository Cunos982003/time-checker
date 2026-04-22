package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.position.PositionRequest;
import com.apex.timekeeping.domain.dto.position.PositionResponse;

import java.util.List;

public interface IPositionService {
    List<PositionResponse> findAll();
    PositionResponse create(PositionRequest req);
    PositionResponse update(Long id, PositionRequest req);
}
