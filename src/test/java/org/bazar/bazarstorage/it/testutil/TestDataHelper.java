package org.bazar.bazarstorage.it.testutil;


import builder.StorageNodeBuilder;
import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.adapter.outbound.persistence.storagenode.StorageNodeJpaRepository;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeType;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeUploadStatus;
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

    public StorageNode createStorageNodeWith(StorageNodeUploadStatus status) {
        return storageNodeJpaRepository.save(StorageNodeBuilder.buildWith(status));
    }

    public StorageNode createStorageNodeWith(StorageNodeType type) {
        StorageNode storageNode = StorageNodeBuilder.buildDefault();
        storageNode.setType(type);
        return storageNodeJpaRepository.save(storageNode);
    }

    public StorageNode createStorageNodeWith(StorageNodeStatus status, StorageNodeType type, Long spaceId) {
        StorageNode storageNode = StorageNodeBuilder.buildWith(status);
        storageNode.setType(type);
        storageNode.setSpaceId(spaceId);
        return storageNodeJpaRepository.save(storageNode);
    }

    public StorageNode createStorageNodeWith(StorageNodeStatus status, StorageNodeType type, Long spaceId, StorageNode parent) {
        StorageNode storageNode = StorageNodeBuilder.buildWith(status);
        storageNode.setType(type);
        storageNode.setSpaceId(spaceId);
        storageNode.setParent(parent);
        return storageNodeJpaRepository.save(storageNode);
    }

    public StorageNode createStorageNodeWith(StorageNodeStatus status) {
        return storageNodeJpaRepository.save(StorageNodeBuilder.buildWith(status));
    }

    public StorageNode createStorageNodeWith(StorageNodeUploadStatus status, List<StorageNodeError> errors) {
        StorageNode storageNode = StorageNodeBuilder.buildWith(status);
        storageNode.setErrors(errors);
        return storageNodeJpaRepository.save(storageNode);
    }

    public StorageNode createStorageNodeWith(UUID fileUuid) {
        return storageNodeJpaRepository.save(StorageNodeBuilder.buildWith(fileUuid));
    }

    public StorageNode createStorageNodeWith(UUID fileUuid, StorageNodeStatus status, StorageNodeType type) {
        StorageNode storageNode = StorageNodeBuilder.buildWith(fileUuid, status);
        storageNode.setType(type);
        return storageNodeJpaRepository.save(storageNode);
    }

    public StorageNode createStorageNodeWith(UUID fileUuid, StorageNodeUploadStatus status) {
        return storageNodeJpaRepository.save(StorageNodeBuilder.buildWith(fileUuid, status));
    }
}
