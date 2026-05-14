package org.bazar.bazarstorage.app.impl.node;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.DeleteNodeInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.impl.helper.StorageNodeStatusChanger;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID;

@Component
@RequiredArgsConstructor
class DeleteNodeUseCase implements DeleteNodeInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeStatusChanger statusChanger;

    @Override
    @Transactional
    public void execute(String nodeId) {
        StorageNode storageNode = storageNodeRepository.findById(Long.parseLong(nodeId))
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_ID, nodeId));
        statusChanger.changeStatus(storageNode, StorageNodeStatus.DELETED);
    }
}
