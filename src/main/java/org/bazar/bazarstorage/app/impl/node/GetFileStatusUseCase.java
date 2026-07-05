package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.GetFileStatusInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.persona.PersonaService;
import org.bazar.bazarstorage.app.api.node.output.AuthorStatus;
import org.bazar.bazarstorage.app.api.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.user.User;
import org.springframework.stereotype.Component;

import static org.bazar.authorization.sdk.Permission.*;
import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID;

@Component
@RequiredArgsConstructor
public class GetFileStatusUseCase implements GetFileStatusInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeMapper storageNodeMapper;
    private final PersonaService personaService;

    @Override
    @Authorize(permission = STORAGE_NODE_READ)
    public FileStatusInfo execute(String spaceId, String nodeId) {
        StorageNode storageNode = storageNodeRepository.findById(Long.parseLong(nodeId))
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_ID, nodeId));
        if (StorageNodeStatus.IN_PROGRESS == storageNode.getStatus()) {
            return storageNodeMapper.toFileStatusInfo(storageNode.getStatus().name());
        }

        User user = personaService.getUserById(storageNode.getUserId()).orElse(null);
        return storageNodeMapper.toFileStatusInfo(storageNode.getStatus().name(), user, AuthorStatus.from(user));
    }
}
