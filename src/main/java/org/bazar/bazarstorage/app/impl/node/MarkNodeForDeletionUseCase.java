package org.bazar.bazarstorage.app.impl.node;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.node.MarkNodeForDeletionInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.impl.helper.StorageNodeStatusChanger;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;

import static org.bazar.authorization.sdk.Permission.*;

@Component
@RequiredArgsConstructor
@Slf4j
class MarkNodeForDeletionUseCase implements MarkNodeForDeletionInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeStatusChanger statusChanger;

    @Override
    @Transactional
    @Authorize(permission = STORAGE_NODE_DELETE)
    public void execute(String spaceId, String nodeId) {
        StorageNode storageNode = storageNodeRepository.findById(Long.parseLong(nodeId)).orElse(null);

        if (storageNode == null) {
            log.warn("Storage node {} not found, cannot delete", nodeId);
            return;
        }

        statusChanger.changeStatus(storageNode, StorageNodeStatus.DELETED);
    }
}
