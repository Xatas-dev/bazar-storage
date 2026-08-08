package org.bazar.bazarstorage.app.impl.helper;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.node.output.NodeErrorInfo;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.FILE_TOO_LARGE;

@Component
@RequiredArgsConstructor
public class ValidatorHelper {
    private final SettingProperties settingProperties;

    public NodeErrorInfo generateNodeErrorInfo(ErrorType errorType, Object... errorParameters) {
        String description = settingProperties.fileValidation().errorMessages().get(errorType);
        return new NodeErrorInfo(FILE_TOO_LARGE.toString(), String.format(description, errorParameters));
    }
}
