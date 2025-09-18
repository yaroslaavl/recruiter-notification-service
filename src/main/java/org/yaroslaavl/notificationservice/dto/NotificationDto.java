package org.yaroslaavl.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record NotificationDto(
        String userId,
        @NotBlank String targetUserId,
        String entityId,
        @NotBlank String entityType,
        @NotBlank String notificationType,
        String content,
        Map<String, String> contentVariables
) { }
