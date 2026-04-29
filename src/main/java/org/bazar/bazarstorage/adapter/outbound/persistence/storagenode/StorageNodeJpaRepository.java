package org.bazar.bazarstorage.adapter.outbound.persistence.storagenode;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StorageNodeJpaRepository extends JpaRepository<StorageNode, Long> {
    Optional<StorageNode> findByFileUuid(UUID fileUuid);
}
