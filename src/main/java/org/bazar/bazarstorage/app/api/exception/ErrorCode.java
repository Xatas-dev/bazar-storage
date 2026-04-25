package org.bazar.bazarstorage.app.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    TECH_ERROR(HttpStatus.CONFLICT, "Technical error");

    @Getter
    private final HttpStatus status;
    private final String messageTemplate;

    ErrorCode(HttpStatus status, String messageTemplate) {
        this.status = status;
        this.messageTemplate = messageTemplate;
    }

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
