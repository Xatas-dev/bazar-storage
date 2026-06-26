package org.bazar.bazarstorage.app.api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ErrorCode {
    TECH_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "Technical error"),
    STORAGE_NODE_NOT_FOUND_BY_FILE_UUID(HttpStatus.NOT_FOUND, "Storage node not found for file UUID: %s"),
    STORAGE_NODE_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, "Storage node not found for ID: %s"),
    NOT_AUTH(HttpStatus.UNAUTHORIZED, "Not authorized"),
    AUTH_DENIED(HttpStatus.FORBIDDEN, "Auth denied"),
    AUTH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Authorization error");

    @Getter
    private final HttpStatus status;
    private final String messageTemplate;

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
