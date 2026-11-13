package org.bazar.bazarstorage.app.impl.validator.piece;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.impl.helper.ValidatorHelper;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.PARENT_NODE_WRONG_SPACE;
import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.PARENT_NODE_WRONG_STATUS;
import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.PARENT_NODE_WRONG_TYPE;

@Component
@RequiredArgsConstructor
public class ParentNodeValidator {
    private final ValidatorHelper validatorHelper;

    public List<StorageNodeError> validate(StorageNode parentNode, Long spaceId) {
        List<StorageNodeError> errors = new ArrayList<>();
        if (parentNode == null) {
            return errors;
        }

        if (StorageNodeType.FOLDER != parentNode.getType()) {
            errors.add(validatorHelper.generateNodeErrorInfo(PARENT_NODE_WRONG_TYPE));
        }
        if (!parentNode.getSpaceId().equals(spaceId)) {
            errors.add(validatorHelper.generateNodeErrorInfo(PARENT_NODE_WRONG_SPACE));
        }
        if (StorageNodeStatus.ACTIVE != parentNode.getStatus()) {
            errors.add(validatorHelper.generateNodeErrorInfo(PARENT_NODE_WRONG_STATUS));
        }

        return errors;
    }
}
