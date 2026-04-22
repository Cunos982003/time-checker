package com.apex.timekeeping.domain.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long notificationId;
    private String title;
    private String content;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
