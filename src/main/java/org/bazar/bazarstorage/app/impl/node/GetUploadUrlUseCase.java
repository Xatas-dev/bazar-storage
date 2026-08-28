package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.auth.AuthenticationService;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.node.GetUploadUrlInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.exception.NodeValidationException;
import org.bazar.bazarstorage.app.api.node.output.InitiateUploadResult;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
import org.bazar.bazarstorage.app.api.validator.StorageNodeValidator;
import org.bazar.bazarstorage.app.impl.helper.FilesHelper;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.bazar.authorization.sdk.Permission.STORAGE_NODE_UPLOAD;

@Slf4j
@RequiredArgsConstructor
@Component
public class GetUploadUrlUseCase implements GetUploadUrlInbound {
    private final StorageNodeValidator storageNodeValidator;
    private final FilesService filesService;
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeMapper storageNodeMapper;
    private final AuthenticationService authenticationService;

    @Override
    @Authorize(spaceIdParam = "#command.spaceId", permission = STORAGE_NODE_UPLOAD)
    public UploadUrlInfo execute(GetUploadUrlCommand command) {
        List<StorageNodeError> validationErrors = storageNodeValidator.validate(
                storageNodeMapper.toFileValidationRequest(command, FilesHelper.getExtensionByFileName(command.fileName()))
        );
        if (!validationErrors.isEmpty()) {
            log.error("Validation errors: {}", validationErrors);
            throw new NodeValidationException(validationErrors);
        }

        InitiateUploadResult initiateUploadResult = filesService.initiateUpload(command);
        UUID userId = authenticationService.getAuthenticatedUserId();
        StorageNode storageNode = storageNodeMapper.toDomain(command, initiateUploadResult, userId);
        storageNodeRepository.save(storageNode);

        return storageNodeMapper.toUploadUrlInfo(initiateUploadResult, storageNode.getId().toString());
    }
}
