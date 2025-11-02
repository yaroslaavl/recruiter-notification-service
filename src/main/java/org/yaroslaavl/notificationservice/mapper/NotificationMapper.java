package org.yaroslaavl.notificationservice.mapper;

import org.mapstruct.Mapper;
import org.yaroslaavl.notificationservice.database.entity.Notification;
import org.yaroslaavl.notificationservice.dto.NotificationShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    List<NotificationShortDto> toNotificationShortDto(List<Notification> notification);
}
