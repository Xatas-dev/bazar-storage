package org.bazar.bazarstorage.app.impl.validator;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.api.validator.FileValidationRequest;
import org.bazar.bazarstorage.app.api.validator.StorageNodeValidator;
import org.bazar.bazarstorage.app.impl.helper.ValidatorHelper;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.FILE_EXTENSION_NOT_ALLOWED;
import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.FILE_NAME_TOO_LARGE;
import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.FILE_TOO_LARGE;

@Component
@RequiredArgsConstructor
public class StorageNodeValidatorImpl implements StorageNodeValidator {
    private final SettingProperties settingProperties;
    private final ValidatorHelper validatorHelper;

    public List<StorageNodeError> validate(FileValidationRequest request) {
        List<StorageNodeError> validationErrors = new ArrayList<>();
        Long maxFileSize = settingProperties.fileValidation().maxFileSize();
        Integer maxFileNameLength = settingProperties.fileValidation().maxFileNameLength();

        if (request.getSize() > maxFileSize) {
            validationErrors.add(validatorHelper.generateNodeErrorInfo(FILE_TOO_LARGE, maxFileSize));
        }
        if (settingProperties.fileValidation().notAllowedExtensions().contains(request.getExtension())) {
            validationErrors.add(validatorHelper.generateNodeErrorInfo(FILE_EXTENSION_NOT_ALLOWED, request.getExtension()));
        }
        if (request.getNodeName().length() > maxFileNameLength) {
            validationErrors.add(validatorHelper.generateNodeErrorInfo(FILE_NAME_TOO_LARGE, maxFileNameLength));
        }
        return validationErrors;
    }
}
