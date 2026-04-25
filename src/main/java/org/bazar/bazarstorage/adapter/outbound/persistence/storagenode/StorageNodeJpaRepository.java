package org.bazar.bazarstorage.adapter.outbound.persistence.storagenode;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageNodeJpaRepository extends JpaRepository<StorageNode, Long> {
}
