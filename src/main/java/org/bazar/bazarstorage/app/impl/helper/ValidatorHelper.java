package org.bazar.bazarstorage.app.impl.helper;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.SettingProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorHelper {
    private final SettingProperties settingProperties;

    public String getErrorMessage(SettingProperties.FileValidation.ErrorType errorType) {
        return settingProperties.getFileValidation().getErrorMessages().get(errorType);
    }
}
