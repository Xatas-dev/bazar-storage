package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.UpdateNodeInbound;
import org.bazar.bazarstorage.app.api.node.commands.UpdateNodeCommand;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.bazar.authorization.sdk.Permission.STORAGE_NODE_UPDATE;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeType.FOLDER;

@Component
@RequiredArgsConstructor
public class UpdateNodeUseCase implements UpdateNodeInbound {
    private final StorageNodeRepository storageNodeRepository;

    @Override
    @Authorize(spaceIdParam = "#command.spaceId", permission = STORAGE_NODE_UPDATE)
    @Transactional
    public void execute(UpdateNodeCommand command) {
        if (!command.parentIdProvided()) {
            return;
        }

        storageNodeRepository.lockSpace(command.spaceId());

        StorageNode storageNode = findNode(command.spaceId(), command.nodeId());
        if (command.parentId() == null) {
            storageNode.setParent(null);
            return;
        }

        StorageNode parentNode = findNode(command.spaceId(), command.parentId());
        validateParent(parentNode);
        if (isSameParent(storageNode, parentNode)) {
            return;
        }
        validateNoCycle(storageNode, parentNode, command.spaceId());

        storageNode.setParent(parentNode);
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private StorageNode findNode(Long spaceId, Long nodeId) {
        return storageNodeRepository.findActiveNode(spaceId, nodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID, nodeId));
    }

    private void validateParent(StorageNode parentNode) {
        if (!FOLDER.equals(parentNode.getType())) {
            throw new BusinessException(ErrorCode.STORAGE_NODE_WRONG_TYPE, parentNode.getId(), parentNode.getType());
        }
    }

    private boolean isSameParent(StorageNode storageNode, StorageNode parentNode) {
        return storageNode.getParent() != null && Objects.equals(storageNode.getParent().getId(), parentNode.getId());
    }

    private void validateNoCycle(StorageNode storageNode, StorageNode parentNode, Long spaceId) {
        if (FOLDER.equals(storageNode.getType())
                && storageNodeRepository.existsInParentChain(spaceId, storageNode.getId(), parentNode.getId())) {
            throw new BusinessException(ErrorCode.STORAGE_NODE_CYCLE_DETECTED, storageNode.getId(), parentNode.getId());
        }
    }
}
