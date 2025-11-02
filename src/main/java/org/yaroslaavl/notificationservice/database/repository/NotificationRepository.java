package org.yaroslaavl.notificationservice.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yaroslaavl.notificationservice.database.entity.Notification;
import org.yaroslaavl.notificationservice.database.entity.enums.NotificationType;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findAllByTargetUserIdAndTypeOrderByCreatedAtDesc(String userKeyId, NotificationType type, Pageable pageable);
}
