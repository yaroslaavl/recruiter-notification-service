package org.yaroslaavl.notificationservice.dto;

import org.yaroslaavl.notificationservice.database.entity.enums.EntityType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationShortDto(
        UUID id,
        String targetUserId,
        UUID entityId,
        EntityType entityType,
        String content,
        Boolean isRead,
        LocalDateTime createdAt
) { }
