package org.yaroslaavl.notificationservice.exception.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ErrorType")
public enum ErrorType {

    ALREADY_REGISTERED,
    EMAIL_VERIFICATION_CODE_NOT_MATCH,
    EMAIL_VERIFICATION_EXPIRED,
    EMAIL_VERIFICATION_FAILED,
}
