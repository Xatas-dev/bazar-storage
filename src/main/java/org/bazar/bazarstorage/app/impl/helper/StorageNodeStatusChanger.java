package org.bazar.bazarstorage.app.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeUploadStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class StorageNodeStatusChanger {
    private static final List<StorageNodeUploadStatus> TERMINAL_UPLOAD_STATUSES = List.of(StorageNodeUploadStatus.ERROR);

    public void changeUploadStatus(StorageNode storageNode, StorageNodeUploadStatus status) {
        StorageNodeUploadStatus oldStatus = storageNode.getUploadStatus();

        if (oldStatus == status) {
            log.warn("StorageNode {} already has uploadStatus {}, no change needed", storageNode.getId(), status);
            return;
        }
        if (TERMINAL_UPLOAD_STATUSES.contains(oldStatus)) {
            log.error("StorageNode {} is in terminal uploadStatus {}, cannot change to {}", storageNode.getId(), oldStatus, status);
            return;
        }
        if (oldStatus == StorageNodeUploadStatus.UPLOADED && status == StorageNodeUploadStatus.IN_PROGRESS) {
            log.error("StorageNode {} has uploadStatus {}, cannot change uploadStatus to {}", storageNode.getId(), oldStatus, status);
            return;
        }

        storageNode.setUploadStatus(status);
        log.info("StorageNode {} uploadStatus changed from {} to {}", storageNode.getId(), oldStatus, status);
    }

    public void changeStatus(StorageNode storageNode, StorageNodeStatus status) {
        storageNode.setStatus(status);
    }
}
