package org.bazar.bazarstorage.app.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class StorageNodeStatusChanger {
    private static final List<StorageNodeStatus> TERMINAL_STATUSES =
            List.of(StorageNodeStatus.ERROR, StorageNodeStatus.DELETED);

    public void changeStatus(StorageNode storageNode, StorageNodeStatus status) {
        StorageNodeStatus oldStatus = storageNode.getStatus();

        if (oldStatus == status) {
            log.warn("StorageNode {} already has status {}, no change needed", storageNode.getId(), status);
            return;
        }
        if (TERMINAL_STATUSES.contains(oldStatus)) {
            log.error("StorageNode {} is in terminal status {}, cannot change to {}", storageNode.getId(), oldStatus, status);
            return;
        }
        if (oldStatus == StorageNodeStatus.UPLOADED && status == StorageNodeStatus.IN_PROGRESS) {
            log.error("StorageNode {} has status {}, cannot change status to {}", storageNode.getId(), oldStatus, status);
            return;
        }

        storageNode.setStatus(status);
        log.info("StorageNode {} status changed from {} to {}", storageNode.getId(), oldStatus, status);
    }
}
