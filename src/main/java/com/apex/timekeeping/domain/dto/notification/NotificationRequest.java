package com.apex.timekeeping.domain.dto.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
