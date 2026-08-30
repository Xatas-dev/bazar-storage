package org.bazar.bazarstorage.app.impl.helper;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorHelper {
    private final SettingProperties settingProperties;

    public StorageNodeError generateNodeErrorInfo(ErrorType errorType, Object... errorParameters) {
        String description = settingProperties.fileValidation().errorMessages().get(errorType);
        return new StorageNodeError(errorType.toString(), String.format(description, errorParameters));
    }
}
