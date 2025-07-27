package org.yaroslaavl.notificationservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailCandidateValidator.class)
public @interface EmailCandidate {

    String message() default "{email.candidate}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
