package org.bazar.bazarstorage.app.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("settings")
public record SettingProperties(
    FileValidation fileValidation,
    Schedule schedule
) {
    public record FileValidation(
        Long maxFileSize,
        List<String> notAllowedExtensions,
        Integer maxFileNameLength,
        Map<ErrorType, String> errorMessages
    ) {
        public enum ErrorType {
            FILE_TOO_LARGE,
            FILE_EXTENSION_NOT_ALLOWED,
            FILE_NAME_TOO_LARGE
        }
    }

    public record Schedule(
        DeleteMarkedFiles deleteMarkedFiles
    ) {
        public record DeleteMarkedFiles(
                Integer batchSize,
                Integer retentionDays
        ) {}
    }
}
