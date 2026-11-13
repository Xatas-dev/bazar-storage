package org.bazar.bazarstorage.app.impl.validator;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.node.commands.CreateNodeCommand;
import org.bazar.bazarstorage.app.impl.validator.piece.NodeNameValidator;
import org.bazar.bazarstorage.app.impl.validator.piece.ParentNodeValidator;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateFolderNodeValidator {
    private final NodeNameValidator nodeNameValidator;
    private final ParentNodeValidator parentNodeValidator;

    public List<StorageNodeError> validate(CreateNodeCommand command, StorageNode parentNode) {
        List<StorageNodeError> errors = new ArrayList<>();

        errors.addAll(nodeNameValidator.validate(command.name()));
        errors.addAll(parentNodeValidator.validate(parentNode, command.spaceId()));

        return errors;
    }
}
