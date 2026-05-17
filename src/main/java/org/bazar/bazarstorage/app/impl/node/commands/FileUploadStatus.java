package org.bazar.bazarstorage.app.impl.node.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FileUploadStatus {
    UPLOADED,
    ERROR,
    UNKNOWN;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static FileUploadStatus fromString(String value) {
        try {
            return FileUploadStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return UNKNOWN;
        }
    }
}

