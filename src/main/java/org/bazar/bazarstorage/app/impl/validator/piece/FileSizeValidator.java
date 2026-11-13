package org.bazar.bazarstorage.app.impl.validator.piece;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.impl.helper.ValidatorHelper;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.FILE_TOO_LARGE;

@Component
@RequiredArgsConstructor
public class FileSizeValidator {
    private final SettingProperties settingProperties;
    private final ValidatorHelper validatorHelper;

    public List<StorageNodeError> validate(Long size) {
        List<StorageNodeError> errors = new ArrayList<>();

        Long maxFileSize = settingProperties.fileValidation().maxFileSize();
        if (size > maxFileSize) {
            errors.add(validatorHelper.generateNodeErrorInfo(FILE_TOO_LARGE, maxFileSize / 1024 / 1024));
        }

        return errors;
    }
}
