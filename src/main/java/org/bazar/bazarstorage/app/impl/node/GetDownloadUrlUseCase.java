package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.authorization.sdk.AuthorizationRequest;
import org.bazar.bazarstorage.app.api.auth.AuthenticationService;
import org.bazar.bazarstorage.app.api.auth.AuthorizationService;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.node.GetDownloadUrlInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.GetDownloadUrlCommand;
import org.bazar.bazarstorage.app.impl.helper.AuthorizationRequestBuilder;
import org.bazar.bazarstorage.app.api.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus.ACTIVE;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeType.FILE;

@Component
@RequiredArgsConstructor
public class GetDownloadUrlUseCase implements GetDownloadUrlInbound {
    private final FilesService filesService;
    private final StorageNodeMapper storageNodeMapper;
    private final StorageNodeRepository storageNodeRepository;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;

    @Override
    public DownloadUrlInfo execute(GetDownloadUrlCommand command) {
        StorageNode storageNode = storageNodeRepository.findById(command.nodeId())
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_ID, command.nodeId()));
        validateStorageNodeForDownloading(storageNode);
        checkDownloadPermissions(command.spaceId(), storageNode);
        String downloadUrl = filesService.initiateDownload(storageNode.getFileUuid());
        return storageNodeMapper.toDownloadUrlInfo(downloadUrl);
    }

    // =================================================================================================================
    // Implementation
    // =================================================================================================================

    private void checkDownloadPermissions(Long spaceId, StorageNode storageNode) {
        AuthorizationRequest authorizationRequest = AuthorizationRequestBuilder.buildNodeDownloadRequest(
                spaceId,
                authenticationService.getCurrentJwtToken(),
                storageNode
        );

        authorizationService.authorize(authorizationRequest);
    }

    private void validateStorageNodeForDownloading(StorageNode storageNode) {
        if (!FILE.equals(storageNode.getType())) {
            throw new BusinessException(ErrorCode.STORAGE_NODE_WRONG_TYPE, storageNode.getId(), storageNode.getType());
        }
        if (!ACTIVE.equals(storageNode.getStatus())) {
            throw new BusinessException(ErrorCode.STORAGE_NODE_WRONG_STATUS, storageNode.getId(), storageNode.getStatus());
        }
    }
}
