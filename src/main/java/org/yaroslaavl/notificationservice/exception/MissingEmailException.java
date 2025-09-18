package org.yaroslaavl.notificationservice.exception;

public class MissingEmailException extends RuntimeException {
    public MissingEmailException(String message) {
        super(message);
    }
}
