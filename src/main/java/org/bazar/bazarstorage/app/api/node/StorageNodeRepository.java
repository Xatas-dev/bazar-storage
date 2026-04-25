package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;

public interface StorageNodeRepository {
    void save(StorageNode storageNode);
}
