package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;

import java.util.Optional;

public interface StorageNodeRepository {
    void save(StorageNode storageNode);

    Optional<StorageNode> findByFileUuid(String fileUuid);
}
