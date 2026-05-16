package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StorageNodeRepository {
    void save(StorageNode storageNode);

    Optional<StorageNode> findByFileUuid(String fileUuid);

    Page<StorageNode> findBySpaceIdAndStatus(Long spaceId, StorageNodeStatus status, Pageable pageable);

    Optional<StorageNode> findById(Long nodeId);

    List<StorageNode> findDeletedNodesAfterId(Long lastId, Integer batch);

    void deleteAllByIds(List<Long> ids);
}
