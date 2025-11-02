package org.yaroslaavl.notificationservice.dto;

import org.yaroslaavl.notificationservice.database.entity.enums.EntityType;
import org.yaroslaavl.notificationservice.database.entity.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationShortDto(
        UUID id,
        String targetUserId,
        UUID entityId,
        EntityType entityType,
        String content,
        NotificationType type,
        Boolean isRead,
        LocalDateTime createdAt
) { }
