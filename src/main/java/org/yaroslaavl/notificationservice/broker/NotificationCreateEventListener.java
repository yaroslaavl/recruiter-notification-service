package org.yaroslaavl.notificationservice.broker;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.yaroslaavl.notificationservice.dto.NotificationDto;
import org.yaroslaavl.notificationservice.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCreateEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queues.user.name}", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserEmailMessage(NotificationDto notificationDto) {
        processMessage(notificationDto, "email");
    }

    @RabbitListener(queues = "${rabbitmq.queues.recruiting.name}", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserInAppMessage(NotificationDto notificationDto) {
        processMessage(notificationDto, "in-app");
    }

    @RabbitListener(queues = "${rabbitmq.queues.communication.name}", containerFactory = "rabbitListenerContainerFactory")
    public void handleCommunicationEmailMessage(NotificationDto notificationDto) {
        processMessage(notificationDto, "email");
    }

    private void processMessage(NotificationDto dto, String channel) {
        log.info("Received {} notification", channel);
        try {
            notificationService.create(dto);
        } catch (Exception e) {
            log.error("Failed to process {} notification: {}", channel, e.getMessage(), e);
            throw e;
        }
    }
}
