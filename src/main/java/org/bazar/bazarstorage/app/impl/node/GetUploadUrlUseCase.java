package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.SettingProperties;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.node.GetUploadUrlInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.exception.FileValidationException;
import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.helper.FilesHelper;
import org.bazar.bazarstorage.app.impl.helper.ValidatorHelper;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.bazar.bazarstorage.app.service.AuthenticationService;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bazar.bazarstorage.app.api.SettingProperties.FileValidation.ErrorType.FILE_EXTENSION_NOT_ALLOWED;
import static org.bazar.bazarstorage.app.api.SettingProperties.FileValidation.ErrorType.FILE_NAME_TOO_LARGE;
import static org.bazar.bazarstorage.app.api.SettingProperties.FileValidation.ErrorType.FILE_TOO_LARGE;

@Slf4j
@RequiredArgsConstructor
@Component
public class GetUploadUrlUseCase implements GetUploadUrlInbound {
    private final SettingProperties settingProperties;
    private final ValidatorHelper validatorHelper;
    private final FilesService filesService;
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeMapper storageNodeMapper;
    private final AuthenticationService authenticationService;

    @Override
    public UploadUrlInfo execute(GetUploadUrlCommand command) {
        // TODO: интеграция с auth. Сделать в рамках https://grinbog015.atlassian.net/browse/BZR-103
        List<String> validationErrors = validateFile(command);
        if (!validationErrors.isEmpty()) {
            log.error("Validation errors: {}", validationErrors);
            throw new FileValidationException(validationErrors);
        }

        UploadUrlInfo uploadUrlInfo = filesService.initiateUpload(command);
        UUID userId = authenticationService.getAuthenticatedUserId();
        StorageNode storageNode = storageNodeMapper.toDomain(command, uploadUrlInfo, userId);
        storageNodeRepository.save(storageNode);

        return uploadUrlInfo;
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    // TODO: подумать над вынесением в отдельный класс с последующим расширением под валидацию папок. Будет реализовано в рамках стори https://grinbog015.atlassian.net/browse/BZR-104
    private List<String> validateFile(GetUploadUrlCommand command) {
        List<String> validationErrors = new ArrayList<>();
        Long maxFileSize = settingProperties.getFileValidation().getMaxFileSize();
        Integer maxFileNameLength = settingProperties.getFileValidation().getMaxFileNameLength();
        String extension = FilesHelper.getExtension(command.fileName());

        if (command.size() > maxFileSize) {
            validationErrors.add(String.format(validatorHelper.getErrorMessage(FILE_TOO_LARGE), maxFileSize));
        }
        if (settingProperties.getFileValidation().getNotAllowedExtensions().contains(extension)) {
            validationErrors.add(String.format(validatorHelper.getErrorMessage(FILE_EXTENSION_NOT_ALLOWED), extension));
        }
        if (command.fileName().length() > maxFileNameLength) {
            validationErrors.add(String.format(validatorHelper.getErrorMessage(FILE_NAME_TOO_LARGE), maxFileNameLength));
        }
        return validationErrors;
    }
}
