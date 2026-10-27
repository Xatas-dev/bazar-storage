package org.bazar.bazarstorage.app.impl.validator.piece;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.impl.helper.ValidatorHelper;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.NODE_NAME_TOO_LONG;

@Component
@RequiredArgsConstructor
public class NodeNameValidator {
    private final SettingProperties settingProperties;
    private final ValidatorHelper validatorHelper;

    public List<StorageNodeError> validate(String name) {
        List<StorageNodeError> errors = new ArrayList<>();

        Integer maxNodeNameLength = settingProperties.fileValidation().maxNodeNameLength();
        if (name.length() > maxNodeNameLength) {
            errors.add(validatorHelper.generateNodeErrorInfo(NODE_NAME_TOO_LONG, maxNodeNameLength));
        }

        return errors;
    }
}
