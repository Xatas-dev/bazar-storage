package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorageNodeRepository {
    void save(StorageNode storageNode);

    Optional<StorageNode> findByFileUuid(String fileUuid);

    Page<StorageNode> findActiveNodes(Long spaceId, Pageable pageable);

    Optional<StorageNode> findById(Long nodeId);

    List<StorageNode> findDeletedNodesAfterIdWithRetention(Long lastId, Instant retentionThreshold, Integer batchSize);

    void deleteAllByIds(List<Long> ids);

    int updateFileUuid(Long nodeId, UUID fileUuid);
}
