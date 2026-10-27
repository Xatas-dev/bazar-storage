package org.bazar.bazarstorage.adapter.outbound.persistence.storagenode;

import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeType;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeUploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorageNodeJpaRepository extends JpaRepository<StorageNode, Long> {
    Optional<StorageNode> findByFileUuid(UUID fileUuid);

    @Query("""
    SELECT sn FROM StorageNode sn
    WHERE sn.spaceId = :spaceId AND sn.status = :status AND (sn.type = :type OR sn.uploadStatus = :uploadStatus)
    """)
    Page<StorageNode> findBySpaceIdAndStatusAndUploadedStatusOrType(Long spaceId, StorageNodeStatus status,
            StorageNodeUploadStatus uploadStatus, StorageNodeType type, Pageable pageable);

    @Query(value = """
    SELECT *
    FROM storage_node sn
    WHERE sn.status = :status
          AND sn.id > :lastId
          AND sn.updated_at <= :retentionThreshold
    ORDER BY sn.id
    LIMIT :batch
    """, nativeQuery = true)
    List<StorageNode> findNodesByStatusAfterIdWithRetentionAndLimit(
            Long lastId, String status, Instant retentionThreshold, Integer batch);

    @Modifying
    @Query("""
    UPDATE StorageNode sn
    SET sn.fileUuid = :fileUuid
    WHERE sn.id = :nodeId
    """)
    int updateFileUuid(@Param("nodeId") Long nodeId, @Param("fileUuid") UUID fileUuid);
}
