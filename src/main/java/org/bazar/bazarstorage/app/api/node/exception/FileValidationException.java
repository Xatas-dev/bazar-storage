package org.bazar.bazarstorage.app.api.node.exception;

import lombok.Getter;

import java.util.List;

// TODO: подумать над добавлением общего класса ошибки NodeValidationException, чтобы потом можно было удобнее валидировать папки
@Getter
public class FileValidationException extends RuntimeException {
    private final List<String> validationErrors;

    public FileValidationException(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
