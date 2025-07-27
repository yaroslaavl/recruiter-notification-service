package org.yaroslaavl.notificationservice.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaroslaavl.notificationservice.dto.ErrorResponse;
import org.yaroslaavl.notificationservice.exception.EmailException;
import org.yaroslaavl.notificationservice.exception.error.ErrorType;

import java.util.Optional;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Set<String> emailKeyErrors = Set.of("registered", "match", "expired", "failed");

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponse> handleEmail(EmailException e, HttpServletRequest request) {
        ErrorType errorType = emailKeyError(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                errorType,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private ErrorType emailKeyError(String message) {
        Optional<String> errorType = emailKeyErrors.stream()
                .filter(word -> message.toLowerCase().contains(word.toLowerCase()))
                .findFirst();

        if (errorType.isPresent()) {
            switch (errorType.get()) {
                case "registered" -> {
                    return ErrorType.ALREADY_REGISTERED;
                }
                case "match" -> {
                    return ErrorType.EMAIL_VERIFICATION_CODE_NOT_MATCH;
                }
                case "expired" -> {
                    return ErrorType.EMAIL_VERIFICATION_EXPIRED;
                }
                case "failed" -> {
                    return ErrorType.EMAIL_VERIFICATION_FAILED;
                }
            }
        }

        return null;
    }
}
