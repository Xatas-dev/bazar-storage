package org.bazar.bazarstorage.adapter.outbound.persistence.storagenode;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorageNodeJpaRepository extends JpaRepository<StorageNode, Long> {
    Optional<StorageNode> findByFileUuid(UUID fileUuid);

    Page<StorageNode> findBySpaceIdAndStatus(Long spaceId, StorageNodeStatus status, Pageable pageable);

    @Query(value = """
    SELECT *
    FROM storage_node sn
    WHERE sn.status = :status AND sn.id > :lastId
    ORDER BY sn.id
    LIMIT :batch
    """, nativeQuery = true)
    List<StorageNode> findNodesByStatusAfterIdWithLimit(Long lastId, String status, Integer batch);

    @Query(value = """
    SELECT *
    FROM storage_node sn
    WHERE sn.status = :status
          AND sn.id > :lastId
          AND sn.created_at <= :retentionThreshold
    ORDER BY sn.id
    LIMIT :batch
    """, nativeQuery = true)
    List<StorageNode> findNodesByStatusAfterIdWithRetentionAndLimit(
            Long lastId, String status, Instant retentionThreshold, Integer batch);
}
