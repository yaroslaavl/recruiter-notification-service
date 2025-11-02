package org.yaroslaavl.notificationservice.database.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "EntityType")
public enum EntityType {
    // ==== SYSTEM EVENTS ====
    SYSTEM(null, null),

    SYSTEM_USER_REGISTRATION("Hello, [[firstName]]. Thank you for registering on our website.", List.of("firstName")),

    // ==== VACANCY EVENTS ====
    VACANCY_OPENED_FOR_APPLICATIONS(
            "Vacancy ''[[vacancyTitle]]'' is now open for applications [[applicationDeadline]].",
            List.of("vacancyTitle", "applicationDeadline")
    ),

    VACANCY_TEMPORARY_BLOCKED(
            "Vacancy '[[vacancyTitle]]' has been temporarily blocked. You reached maximum of reports: [[reports]]",
            List.of("vacancyTitle", "reports")
    ),

    VACANCY_EXPIRED(
            "Vacancy '[[vacancyTitle]]' has expired on [[expiredAt]].",
            List.of("vacancyTitle", "expiredAt")
    ),

    VACANCY_REPORTED(
            "Vacancy '[[vacancyTitle]]' was reported. Reason: [[reason]]. Thank you for informing us about violations.",
            List.of("vacancyTitle", "reason")
    ),

    VACANCY_CREATED(
            "Vacancy '[[vacancyTitle]]' was created on [[createdAt]].",
            List.of("vacancyTitle", "createdAt")
    ),

    // ==== APPLICATION EVENTS ====
    APPLICATION_SUBMITTED(
            "Application submitted for vacancy '[[vacancyTitle]]' on [[submittedAt]].",
            List.of("vacancyTitle", "submittedAt")
    ),

    APPLICATION_APPROVED(
            "Application for vacancy '[[vacancyTitle]]' was approved on [[approvedAt]].",
            List.of("vacancyTitle", "approvedAt")
    ),

    APPLICATION_STATUS_CHANGED(
            "Application for vacancy '[[vacancyTitle]]' changed status from [[oldStatus]] to [[newStatus]] on [[changedAt]].",
            List.of("vacancyTitle", "oldStatus", "newStatus", "changedAt")
    );

    private final String messageTemplate;
    private final List<String> parameters;

    EntityType(String messageTemplate, List<String> parameters) {
        this.messageTemplate = messageTemplate;
        this.parameters = parameters;
    }
}