package org.bazar.bazarstorage.adapter.outbound.persistence.storagenode;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

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
}
