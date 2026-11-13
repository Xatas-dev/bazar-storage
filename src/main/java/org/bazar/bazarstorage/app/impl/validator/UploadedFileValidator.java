package org.bazar.bazarstorage.app.impl.validator;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadResultCommand;
import org.bazar.bazarstorage.app.impl.helper.FilesHelper;
import org.bazar.bazarstorage.app.impl.validator.piece.FileExtensionValidator;
import org.bazar.bazarstorage.app.impl.validator.piece.FileSizeValidator;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UploadedFileValidator {
    private final FileExtensionValidator fileExtensionValidator;
    private final FileSizeValidator fileSizeValidator;

    public List<StorageNodeError> validate(FileUploadResultCommand command) {
        List<StorageNodeError> errors = new ArrayList<>();

        errors.addAll(fileExtensionValidator.validate(FilesHelper.getExtensionFromContentType(command.contentType())));
        errors.addAll(fileSizeValidator.validate(command.size()));

        return errors;
    }
}
