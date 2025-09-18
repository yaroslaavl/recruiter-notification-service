package org.yaroslaavl.notificationservice.database.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.yaroslaavl.notificationservice.database.entity.enums.EntityType;
import org.yaroslaavl.notificationservice.database.entity.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notification", schema = "notification_data")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "user_id")
    UUID userId;

    @Column(name = "target_user_id", nullable = false)
    UUID targetUserId;

    @Column(name = "entity_id")
    UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    EntityType entityType;

    @Column(name = "content", columnDefinition = "TEXT")
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    NotificationType type;

    @Column(name = "is_read")
    Boolean isRead;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;
}
