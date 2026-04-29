package org.bazar.bazarstorage.app.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    TECH_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "Technical error"),
    STORAGE_NODE_NOT_FOUND_BY_FILE_UUID(HttpStatus.NOT_FOUND, "Storage node not found for file UUID: %s");

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
