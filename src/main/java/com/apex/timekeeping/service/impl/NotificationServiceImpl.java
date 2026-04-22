package com.apex.timekeeping.service.impl;

import com.apex.timekeeping.common.exception.ResourceNotFoundException;
import com.apex.timekeeping.domain.dto.notification.NotificationRequest;
import com.apex.timekeeping.domain.dto.notification.NotificationResponse;
import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.Notification;
import com.apex.timekeeping.domain.repository.EmployeeRepository;
import com.apex.timekeeping.domain.repository.NotificationRepository;
import com.apex.timekeeping.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository     employeeRepository;
    private final ModelMapper            modelMapper;

    @Override
    public Page<NotificationResponse> findAll(Pageable pageable) {
        return notificationRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public NotificationResponse create(Long creatorId, NotificationRequest req) {
        Employee creator = employeeRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Notification n = new Notification();
        n.setTitle(req.getTitle());
        n.setContent(req.getContent());
        n.setCreatedBy(creator);
        return toResponse(notificationRepository.save(n));
    }

    @Override
    @Transactional
    public NotificationResponse update(Long id, NotificationRequest req) {
        Notification n = getOrThrow(id);
        n.setTitle(req.getTitle());
        n.setContent(req.getContent());
        return toResponse(notificationRepository.save(n));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Notification n = getOrThrow(id);
        n.setIsActive(false);
        notificationRepository.save(n);
    }

    private Notification getOrThrow(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse res = modelMapper.map(n, NotificationResponse.class);
        res.setCreatedByName(n.getCreatedBy().getFullname());
        return res;
    }
}
