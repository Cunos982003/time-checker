package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeRequest;
import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeResponse;
import com.apex.timekeeping.domain.entity.LeaveType;
import com.apex.timekeeping.domain.repository.LeaveTypeRepository;
import com.apex.timekeeping.service.ILeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements ILeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final ModelMapper         modelMapper;

    @Override
    public List<LeaveTypeResponse> findAll() {
        return leaveTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<LeaveTypeResponse> findActive() {
        return leaveTypeRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public LeaveTypeResponse create(LeaveTypeRequest req) {
        if (leaveTypeRepository.findByTypeCode(req.getTypeCode()).isPresent())
            throw new BusinessException("Leave type code already exists: " + req.getTypeCode());
        LeaveType lt = new LeaveType();
        applyRequest(lt, req);
        return toResponse(leaveTypeRepository.save(lt));
    }

    @Override
    @Transactional
    public LeaveTypeResponse update(Long id, LeaveTypeRequest req) {
        LeaveType lt = getOrThrow(id);
        applyRequest(lt, req);
        return toResponse(leaveTypeRepository.save(lt));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LeaveType lt = getOrThrow(id);
        lt.setIsActive(false);
        leaveTypeRepository.save(lt);
    }

    private void applyRequest(LeaveType lt, LeaveTypeRequest req) {
        modelMapper.map(req, lt);
        if (lt.getIsPaid() == null)           lt.setIsPaid(true);
        if (lt.getRequiresApproval() == null)  lt.setRequiresApproval(true);
        if (lt.getIsActive() == null)          lt.setIsActive(true);
    }

    private LeaveType getOrThrow(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found: " + id));
    }

    private LeaveTypeResponse toResponse(LeaveType lt) {
        return modelMapper.map(lt, LeaveTypeResponse.class);
    }
}
