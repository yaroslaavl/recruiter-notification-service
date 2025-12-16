package org.yaroslaavl.notificationservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.yaroslaavl.notificationservice.database.entity.Notification;
import org.yaroslaavl.notificationservice.database.entity.enums.EntityType;
import org.yaroslaavl.notificationservice.database.entity.enums.NotificationType;
import org.yaroslaavl.notificationservice.database.repository.NotificationRepository;
import org.yaroslaavl.notificationservice.dto.NotificationDto;
import org.yaroslaavl.notificationservice.dto.NotificationShortDto;
import org.yaroslaavl.notificationservice.dto.PageShortDto;
import org.yaroslaavl.notificationservice.exception.MissingEmailException;
import org.yaroslaavl.notificationservice.feignClient.user.UserFeignClient;
import org.yaroslaavl.notificationservice.mapper.NotificationMapper;
import org.yaroslaavl.notificationservice.service.EmailService;
import org.yaroslaavl.notificationservice.service.NotificationService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final NotificationMapper notificationMapper;
    private final UserFeignClient client;

    private static final Map<String, String> APPLICATION_STATUSES = Map.of(
            "NO_MORE_INTERESTS", "No more interests",
            "VIEWED", "Viewed",
            "IN_PROGRESS", "In progress",
            "ACCEPTED", "Accepted",
            "RESOLVED", "Resolved",
            "NEW", "New",
            "REJECTED", "Rejected"
    );

    @Override
    @Transactional
    public Notification create(NotificationDto notificationDto) {
        NotificationType notificationType = NotificationType.valueOf(notificationDto.notificationType());
        EntityType entityType = EntityType.valueOf(notificationDto.entityType());
        Map<String, String> requestedVariables = notificationDto.contentVariables();
        Map<String, String> variables = new HashMap<>(requestedVariables);

        if (entityType == EntityType.APPLICATION_STATUS_CHANGED) {
            for (Map.Entry<String, String> entry : requestedVariables.entrySet()) {
                variables.put(entry.getKey(), APPLICATION_STATUSES.getOrDefault(entry.getValue(), entry.getValue()));
            }
        }

        String content = renderContent(notificationDto.content(), variables, notificationType, entityType);

        Notification notification = Notification.builder()
                .userId(notificationDto.userId() != null ? notificationDto.userId() : null)
                .targetUserId(notificationDto.targetUserId())
                .entityId(notificationDto.entityId() != null ? UUID.fromString(notificationDto.entityId()) : null)
                .entityType(entityType)
                .content(content)
                .type(notificationType)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.saveAndFlush(notification);
        String to;
        if (notificationType == NotificationType.EMAIL) {
             to = Optional.of(notificationDto.contentVariables())
                    .map(vars -> vars.get("email"))
                    .orElse(null);

            if (to == null || to.isBlank()) {
                to = client.getUserShortInfo(notificationDto.targetUserId()).email();

                if (to == null || to.isBlank()) {
                    log.error("Missing 'email' in contentVariables: {}", notificationDto.contentVariables());
                    throw new MissingEmailException("Missing email in contentVariables");
                }
            }

            try {
                emailService.sendEmail(
                        to,
                        notificationDto.content() != null ? notificationDto.content() : "Notification",
                        notification.getContent()
                );
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", to, e.getMessage());
            }
        }

        return notification;
    }

    @Override
    public PageShortDto<NotificationShortDto> mine(String userKeyId, Pageable pageable) {
        log.info("Attempting to fetch notifications for userId: {}", userKeyId);

        Page<Notification> userNotifications
                = notificationRepository.findAllByTargetUserIdAndTypeOrderByCreatedAtDesc(userKeyId, NotificationType.DASHBOARD_APP, pageable);

        if (userNotifications.getContent().isEmpty()) {
            return new PageShortDto<>(Collections.emptyList(), 0, 0, 0, 0);
        }

        return  new PageShortDto<>(
                notificationMapper.toNotificationShortDto(userNotifications.getContent()),
                userNotifications.getTotalElements(),
                userNotifications.getTotalPages(),
                userNotifications.getNumber(),
                userNotifications.getNumberOfElements()
        );
    }


    private String renderContent(String content, Map<String, String> variables, NotificationType notificationType, EntityType entityType) {

        if (variables == null) {
            return content;
        }

        String messageBody = "";
        if (notificationType == NotificationType.EMAIL) {
            messageBody = loadContent(content);
        } else if (notificationType == NotificationType.DASHBOARD_APP) {
            messageBody = entityType.getMessageTemplate();
        }

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            messageBody = messageBody.replace(targetToReplace(notificationType, entry.getKey()), entry.getValue());
        }
        return messageBody;
    }

    private String loadContent(String contentName) {
        ClassPathResource classPathResource = new ClassPathResource("content/" + contentName + ".html");
        try {
            return StreamUtils.copyToString(classPathResource.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private String targetToReplace(NotificationType notificationType, String target) {
        if (notificationType == NotificationType.EMAIL) {
            return "{{" + target + "}}";
        } else  if (notificationType == NotificationType.DASHBOARD_APP) {
            return "[[" + target + "]]";
        }

        return target;
    }
}
