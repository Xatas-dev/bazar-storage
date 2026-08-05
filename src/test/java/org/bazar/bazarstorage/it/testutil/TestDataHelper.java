package org.bazar.bazarstorage.it.testutil;


import builder.StorageNodeBuilder;
import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.adapter.outbound.persistence.storagenode.StorageNodeJpaRepository;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataHelper {
    private final StorageNodeJpaRepository storageNodeJpaRepository;

    public void clearTables() {
        storageNodeJpaRepository.deleteAll();
    }

    public StorageNode createStorageNodeWith(StorageNodeStatus status) {
        return storageNodeJpaRepository.save(StorageNodeBuilder.buildWith(status));
    }

    public StorageNode createStorageNodeWith(StorageNodeStatus status, List<StorageNodeError> errors) {
        StorageNode storageNode = StorageNodeBuilder.buildWith(status);
        storageNode.setErrors(errors);
        return storageNodeJpaRepository.save(storageNode);
    }

    public StorageNode createStorageNodeWith(UUID fileUuid) {
        return storageNodeJpaRepository.save(StorageNodeBuilder.buildWith(fileUuid));
    }

    public StorageNode createStorageNodeWith(UUID fileUuid, StorageNodeStatus status) {
        return storageNodeJpaRepository.save(StorageNodeBuilder.buildWith(fileUuid, status));
    }
}
