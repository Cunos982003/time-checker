package com.apex.timekeeping.service;

import com.apex.timekeeping.common.exception.BusinessException;
import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeRequest;
import com.apex.timekeeping.domain.dto.leavetype.LeaveTypeResponse;
import com.apex.timekeeping.domain.entity.LeaveType;
import com.apex.timekeeping.domain.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public List<LeaveTypeResponse> findAll() {
        return leaveTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<LeaveTypeResponse> findActive() {
        return leaveTypeRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Transactional
    public LeaveTypeResponse create(LeaveTypeRequest req) {
        if (leaveTypeRepository.findByTypeCode(req.getTypeCode()).isPresent()) {
            throw new BusinessException("Leave type code already exists: " + req.getTypeCode());
        }
        LeaveType lt = new LeaveType();
        applyRequest(lt, req);
        return toResponse(leaveTypeRepository.save(lt));
    }

    @Transactional
    public LeaveTypeResponse update(Long id, LeaveTypeRequest req) {
        LeaveType lt = getOrThrow(id);
        applyRequest(lt, req);
        return toResponse(leaveTypeRepository.save(lt));
    }

    @Transactional
    public void delete(Long id) {
        LeaveType lt = getOrThrow(id);
        lt.setIsActive(false);
        leaveTypeRepository.save(lt);
    }

    private void applyRequest(LeaveType lt, LeaveTypeRequest req) {
        lt.setTypeCode(req.getTypeCode());
        lt.setTypeName(req.getTypeName());
        lt.setMaxDaysPerYear(req.getMaxDaysPerYear());
        lt.setIsPaid(req.getIsPaid() != null ? req.getIsPaid() : true);
        lt.setRequiresApproval(req.getRequiresApproval() != null ? req.getRequiresApproval() : true);
        lt.setDescription(req.getDescription());
        lt.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
    }

    private LeaveType getOrThrow(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found: " + id));
    }

    private LeaveTypeResponse toResponse(LeaveType lt) {
        return LeaveTypeResponse.builder()
                .leaveTypeId(lt.getLeaveTypeId())
                .typeCode(lt.getTypeCode())
                .typeName(lt.getTypeName())
                .maxDaysPerYear(lt.getMaxDaysPerYear())
                .isPaid(lt.getIsPaid())
                .requiresApproval(lt.getRequiresApproval())
                .description(lt.getDescription())
                .isActive(lt.getIsActive())
                .build();
    }
}
