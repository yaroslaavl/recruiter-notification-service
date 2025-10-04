package org.yaroslaavl.notificationservice.database.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "NotificationType")
public enum NotificationType {
    /**
     * Email notification.
     **/
    EMAIL,
    /**
     * Notification displayed inside the user app.
     **/
    DASHBOARD_APP,
}
