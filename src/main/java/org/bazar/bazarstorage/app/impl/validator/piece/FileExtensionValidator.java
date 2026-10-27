package org.bazar.bazarstorage.app.impl.validator.piece;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.impl.helper.ValidatorHelper;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.FILE_EXTENSION_NOT_ALLOWED;

@Component
@RequiredArgsConstructor
public class FileExtensionValidator {
    private final SettingProperties settingProperties;
    private final ValidatorHelper validatorHelper;

    public List<StorageNodeError> validate(String extension) {
        List<StorageNodeError> errors = new ArrayList<>();

        if (settingProperties.fileValidation().notAllowedExtensions().contains(extension)) {
            errors.add(validatorHelper.generateNodeErrorInfo(FILE_EXTENSION_NOT_ALLOWED));
        }

        return errors;
    }
}
