package org.bazar.bazarstorage.app.impl.validator;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.node.commands.CreateNodeCommand;
import org.bazar.bazarstorage.app.impl.helper.FilesHelper;
import org.bazar.bazarstorage.app.impl.validator.piece.FileExtensionValidator;
import org.bazar.bazarstorage.app.impl.validator.piece.FileSizeValidator;
import org.bazar.bazarstorage.app.impl.validator.piece.NodeNameValidator;
import org.bazar.bazarstorage.app.impl.validator.piece.ParentNodeValidator;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateFileNodeValidator {
    private final NodeNameValidator nodeNameValidator;
    private final ParentNodeValidator parentNodeValidator;
    private final FileExtensionValidator fileExtensionValidator;
    private final FileSizeValidator fileSizeValidator;

    public List<StorageNodeError> validate(CreateNodeCommand command, StorageNode parentNode) {
        List<StorageNodeError> errors = new ArrayList<>();

        errors.addAll(nodeNameValidator.validate(command.name()));
        errors.addAll(parentNodeValidator.validate(parentNode, command.spaceId()));
        errors.addAll(fileExtensionValidator.validate(FilesHelper.getExtensionByFileName(command.name())));
        errors.addAll(fileSizeValidator.validate(command.size()));

        return errors;
    }
}
