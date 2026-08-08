package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.auth.AuthenticationService;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.node.GetUploadUrlInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.exception.FileValidationException;
import org.bazar.bazarstorage.app.api.node.output.InitiateUploadResult;
import org.bazar.bazarstorage.app.api.node.output.NodeErrorInfo;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.impl.helper.FilesHelper;
import org.bazar.bazarstorage.app.impl.helper.ValidatorHelper;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bazar.authorization.sdk.Permission.STORAGE_NODE_UPLOAD;
import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.*;

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
    @Authorize(spaceIdParam = "#command.spaceId", permission = STORAGE_NODE_UPLOAD)
    public UploadUrlInfo execute(GetUploadUrlCommand command) {
        List<NodeErrorInfo> validationErrors = validateFile(command);
        if (!validationErrors.isEmpty()) {
            log.error("Validation errors: {}", validationErrors);
            throw new FileValidationException(validationErrors);
        }

        InitiateUploadResult initiateUploadResult = filesService.initiateUpload(command);
        UUID userId = authenticationService.getAuthenticatedUserId();
        StorageNode storageNode = storageNodeMapper.toDomain(command, initiateUploadResult, userId);
        storageNodeRepository.save(storageNode);

        return storageNodeMapper.toUploadUrlInfo(initiateUploadResult, storageNode.getId().toString());
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    // TODO: подумать над вынесением в отдельный класс с последующим расширением под валидацию папок. Будет реализовано в рамках стори https://grinbog015.atlassian.net/browse/BZR-104
    private List<NodeErrorInfo> validateFile(GetUploadUrlCommand command) {
        List<NodeErrorInfo> validationErrors = new ArrayList<>();
        Long maxFileSize = settingProperties.fileValidation().maxFileSize();
        Integer maxFileNameLength = settingProperties.fileValidation().maxFileNameLength();
        String extension = FilesHelper.getExtension(command.fileName());

        if (command.size() > maxFileSize) {
            validationErrors.add(validatorHelper.generateNodeErrorInfo(FILE_TOO_LARGE, maxFileSize));
        }
        if (settingProperties.fileValidation().notAllowedExtensions().contains(extension)) {
            validationErrors.add(validatorHelper.generateNodeErrorInfo(FILE_EXTENSION_NOT_ALLOWED, extension));
        }
        if (command.fileName().length() > maxFileNameLength) {
            validationErrors.add(validatorHelper.generateNodeErrorInfo(FILE_NAME_TOO_LARGE, maxFileNameLength));
        }
        return validationErrors;
    }
}
