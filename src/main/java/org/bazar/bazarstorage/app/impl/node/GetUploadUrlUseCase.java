package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.files.command.InitiateUploadCommand;
import org.bazar.bazarstorage.app.api.node.GetUploadUrlInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.InitiateUploadResult;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
import org.bazar.bazarstorage.app.impl.service.script.UpdateStorageNodeFileUuidTransactionalScript;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.bazar.authorization.sdk.Permission.STORAGE_NODE_UPLOAD;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeType.FILE;

@Slf4j
@RequiredArgsConstructor
@Component
public class GetUploadUrlUseCase implements GetUploadUrlInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final FilesService filesService;
    private final StorageNodeMapper storageNodeMapper;
    private final UpdateStorageNodeFileUuidTransactionalScript updateStorageNodeFileUuidTransactionalScript;

    @Override
    @Authorize(spaceIdParam = "#command.spaceId", permission = STORAGE_NODE_UPLOAD)
    public UploadUrlInfo execute(GetUploadUrlCommand command) {
        StorageNode storageNode = storageNodeRepository.findById(command.nodeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID, command.nodeId()));
        if (!FILE.equals(storageNode.getType())) {
            throw new BusinessException(ErrorCode.STORAGE_NODE_WRONG_TYPE_FOR_GETTING_UPLOAD_URL, command.nodeId(), storageNode.getType());
        }

        InitiateUploadCommand initiateUploadCommand = storageNodeMapper.toInitiateUploadCommand(storageNode);
        InitiateUploadResult initiateUploadResult = filesService.initiateUpload(initiateUploadCommand);
        updateStorageNodeFileUuidTransactionalScript.execute(command.nodeId(), UUID.fromString(initiateUploadResult.fileUuid()));

        return storageNodeMapper.toUploadUrlInfo(initiateUploadResult);
    }
}
