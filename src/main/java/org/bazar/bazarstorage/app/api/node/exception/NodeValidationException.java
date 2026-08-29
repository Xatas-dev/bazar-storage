package org.bazar.bazarstorage.app.api.node.exception;

import lombok.Getter;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;

import java.util.List;

@Getter
public class NodeValidationException extends RuntimeException {
    private final List<StorageNodeError> validationErrors;

    public NodeValidationException(List<StorageNodeError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
