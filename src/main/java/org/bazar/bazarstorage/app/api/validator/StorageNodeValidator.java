package org.bazar.bazarstorage.app.api.validator;

import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;

import java.util.List;

public interface StorageNodeValidator {
    List<StorageNodeError> validate(FileValidationRequest request);
}
