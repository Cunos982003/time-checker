package com.apex.timekeeping.service;

import com.apex.timekeeping.domain.dto.notification.NotificationRequest;
import com.apex.timekeeping.domain.dto.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationService {
    Page<NotificationResponse> findAll(Pageable pageable);
    NotificationResponse create(Long creatorId, NotificationRequest req);
    NotificationResponse update(Long id, NotificationRequest req);
    void delete(Long id);
}
