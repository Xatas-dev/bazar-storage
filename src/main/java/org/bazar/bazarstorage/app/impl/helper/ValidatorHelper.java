package org.bazar.bazarstorage.app.impl.helper;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.fw.properties.SettingProperties;
import org.bazar.bazarstorage.fw.properties.SettingProperties.FileValidation.ErrorType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorHelper {
    private final SettingProperties settingProperties;

    public String getErrorMessage(ErrorType errorType) {
        return settingProperties.fileValidation().errorMessages().get(errorType);
    }
}
