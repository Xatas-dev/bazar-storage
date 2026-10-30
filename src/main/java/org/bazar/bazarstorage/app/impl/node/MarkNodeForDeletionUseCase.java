package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.node.MarkNodeForDeletionInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.DeleteNodeCommand;
import org.bazar.bazarstorage.app.impl.helper.StorageNodeStatusChanger;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.bazar.authorization.sdk.Permission.STORAGE_NODE_DELETE;

@Component
@RequiredArgsConstructor
@Slf4j
class MarkNodeForDeletionUseCase implements MarkNodeForDeletionInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeStatusChanger statusChanger;

    @Override
    @Transactional
    @Authorize(spaceIdParam = "#command.spaceId", permission = STORAGE_NODE_DELETE)
    public void execute(DeleteNodeCommand command) {
        storageNodeRepository.lockSpace(command.spaceId());

        List<StorageNode> storageNodes = storageNodeRepository.findAllDescendants(command.nodeId());

        storageNodes.forEach(storageNode -> statusChanger.changeStatus(storageNode, StorageNodeStatus.DELETED));
    }
}
