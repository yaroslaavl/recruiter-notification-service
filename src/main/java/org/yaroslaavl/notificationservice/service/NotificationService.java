package org.yaroslaavl.notificationservice.service;

import org.yaroslaavl.notificationservice.database.entity.Notification;
import org.yaroslaavl.notificationservice.dto.NotificationDto;

public interface NotificationService {

    Notification create(NotificationDto notificationDto);

    /*Page<NotificationShortDto> mine(String userKeyId);*/


}
