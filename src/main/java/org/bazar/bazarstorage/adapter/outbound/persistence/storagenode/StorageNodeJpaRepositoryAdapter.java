package org.bazar.bazarstorage.adapter.outbound.persistence.storagenode;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeType;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeUploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StorageNodeJpaRepositoryAdapter implements StorageNodeRepository {
    private final StorageNodeJpaRepository storageNodeJpaRepository;

    @Override
    public void save(StorageNode storageNode) {
        storageNodeJpaRepository.save(storageNode);
    }

    @Override
    public Optional<StorageNode> findByFileUuid(String fileUuid) {
        return storageNodeJpaRepository.findByFileUuid(UUID.fromString(fileUuid));
    }

    @Override
    public Page<StorageNode> findActiveNodes(Long spaceId, Pageable pageable) {
        return storageNodeJpaRepository.findBySpaceIdAndStatusAndUploadedStatusOrType(
                spaceId, StorageNodeStatus.ACTIVE, StorageNodeUploadStatus.UPLOADED, StorageNodeType.FOLDER, pageable
        );
    }

    @Override
    public Optional<StorageNode> findById(Long nodeId) {
        return storageNodeJpaRepository.findById(nodeId);
    }

    @Override
    public List<StorageNode> findDeletedNodesAfterIdWithRetention(Long lastId,  Instant retentionThreshold, Integer batchSize) {
        return storageNodeJpaRepository.findNodesByStatusAfterIdWithRetentionAndLimit(
                lastId, StorageNodeStatus.DELETED.name(), retentionThreshold, batchSize);
    }

    @Override
    public void deleteAllByIds(List<Long> ids) {
        storageNodeJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public int updateFileUuid(Long nodeId, UUID fileUuid) {
        return storageNodeJpaRepository.updateFileUuid(nodeId, fileUuid);
    }

    @Override
    public List<StorageNode> findAllDescendants(Long nodeId) {
        return storageNodeJpaRepository.findAllWithRecursive(nodeId);
    }
}
