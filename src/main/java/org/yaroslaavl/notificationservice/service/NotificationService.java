package org.yaroslaavl.notificationservice.service;

import org.springframework.data.domain.Pageable;
import org.yaroslaavl.notificationservice.database.entity.Notification;
import org.yaroslaavl.notificationservice.dto.NotificationDto;
import org.yaroslaavl.notificationservice.dto.NotificationShortDto;
import org.yaroslaavl.notificationservice.dto.PageShortDto;

public interface NotificationService {

    Notification create(NotificationDto notificationDto);

    PageShortDto<NotificationShortDto> mine(String userKeyId, Pageable pageable);
}
