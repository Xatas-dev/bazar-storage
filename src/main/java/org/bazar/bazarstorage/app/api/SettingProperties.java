package org.bazar.bazarstorage.app.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties("settings")
public class SettingProperties {
    private FileValidation fileValidation;
    private Schedule schedule;

    @Data
    public static class FileValidation {
        private Long maxFileSize;
        private List<String> notAllowedExtensions;
        private Integer maxFileNameLength;
        private Map<ErrorType, String> errorMessages;

        public enum ErrorType {
            FILE_TOO_LARGE,
            FILE_EXTENSION_NOT_ALLOWED,
            FILE_NAME_TOO_LARGE
        }
    }

    @Data
    public static class Schedule {
        private DeleteMarkedFiles deleteMarkedFiles;

        @Data
        public static class DeleteMarkedFiles {
            private Integer batchSize;
        }
    }
}
