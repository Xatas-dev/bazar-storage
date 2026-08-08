package org.bazar.bazarstorage.app.api.node.exception;

import lombok.Getter;
import org.bazar.bazarstorage.app.api.node.output.NodeErrorInfo;

import java.util.List;

// TODO: подумать над добавлением общего класса ошибки NodeValidationException, чтобы потом можно было удобнее валидировать папки. Будет реализовано в рамках стори https://grinbog015.atlassian.net/browse/BZR-104
@Getter
public class FileValidationException extends RuntimeException {
    private final List<NodeErrorInfo> validationErrors;

    public FileValidationException(List<NodeErrorInfo> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
