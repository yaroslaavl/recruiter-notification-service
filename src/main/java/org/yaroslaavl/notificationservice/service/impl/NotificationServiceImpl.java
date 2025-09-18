package org.yaroslaavl.notificationservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.yaroslaavl.notificationservice.database.entity.Notification;
import org.yaroslaavl.notificationservice.database.entity.enums.EntityType;
import org.yaroslaavl.notificationservice.database.entity.enums.NotificationType;
import org.yaroslaavl.notificationservice.database.repository.NotificationRepository;
import org.yaroslaavl.notificationservice.dto.NotificationDto;
import org.yaroslaavl.notificationservice.exception.MissingEmailException;
import org.yaroslaavl.notificationservice.service.EmailService;
import org.yaroslaavl.notificationservice.service.NotificationService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Notification create(NotificationDto notificationDto) {
        NotificationType notificationType = NotificationType.valueOf(notificationDto.notificationType());
        EntityType entityType = EntityType.valueOf(notificationDto.entityType());

        String content = notificationDto.content();
        if (notificationDto.contentVariables() != null) {
            content = renderContent(notificationDto.content(), notificationDto.contentVariables(), notificationType, entityType);
        }

        Notification notification = Notification.builder()
                .userId(notificationDto.userId() != null ? UUID.fromString(notificationDto.userId()) : null)
                .targetUserId(UUID.fromString(notificationDto.targetUserId()))
                .entityId(notificationDto.entityId() != null ? UUID.fromString(notificationDto.entityId()) : null)
                .entityType(entityType)
                .content(content)
                .type(notificationType)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.saveAndFlush(notification);

        if (notificationType == NotificationType.EMAIL) {
            String to = Optional.ofNullable(notificationDto.contentVariables())
                    .map(vars -> vars.get("email"))
                    .orElse(null);

            if (to == null || to.isBlank()) {
                log.error("Missing 'email' in contentVariables: {}", notificationDto.contentVariables());
                throw new MissingEmailException("Missing email in contentVariables");
            }

            try {
                emailService.sendEmail(
                        to,
                        notificationDto.content() != null ? notificationDto.content() : "Notification",
                        notification.getContent()
                );
                log.info("Email sent to: {}", to);
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", to, e.getMessage());
            }
        }

        return notification;
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
