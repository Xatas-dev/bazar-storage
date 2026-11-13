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
    SELECT sn
    FROM StorageNode sn
    WHERE sn.spaceId = :spaceId
      AND sn.status = :status
      AND sn.parent IS NULL
      AND (
          sn.type = :type
          OR sn.uploadStatus = :uploadStatus
      )
    """)
    Page<StorageNode> findNodesWithoutParent(Long spaceId, StorageNodeStatus status, StorageNodeType type, StorageNodeUploadStatus uploadStatus,
                                    Pageable pageable);

    @Query("""
    SELECT sn
    FROM StorageNode sn
    JOIN sn.parent parent
    WHERE sn.spaceId = :spaceId
      AND sn.status = :status
      AND parent.id = :parentId
      AND parent.status = :parentStatus
      AND parent.type = :parentType
      AND (
          sn.type = :type
          OR sn.uploadStatus = :uploadStatus
      )
    """)
    Page<StorageNode> findChildren(Long spaceId, StorageNodeStatus status, Long parentId, StorageNodeStatus parentStatus,
                                   StorageNodeType parentType, StorageNodeType type, StorageNodeUploadStatus uploadStatus,
                                   Pageable pageable);

    @Query("""
    SELECT sn
    FROM StorageNode sn
    WHERE sn.spaceId = :spaceId
      AND sn.id = :id
      AND sn.status = :status
      AND (
          sn.type = :type
          OR sn.uploadStatus = :uploadStatus
      )
    """)
    Optional<StorageNode> findBySpaceIdAndIdAndStatusAndTypeOrUploadStatus(Long spaceId, Long id, StorageNodeStatus status,
                                                                           StorageNodeType type, StorageNodeUploadStatus uploadStatus);

    @Query(value = """
    WITH RECURSIVE parent_chain AS (
        SELECT id, parent_id
        FROM storage_node
        WHERE id = :parentId
          AND space_id = :spaceId
          AND status = 'ACTIVE'

        UNION

        SELECT sn.id, sn.parent_id
        FROM storage_node sn
        JOIN parent_chain pc ON sn.id = pc.parent_id
        WHERE sn.space_id = :spaceId
          AND sn.status = 'ACTIVE'
    )
    SELECT EXISTS (
        SELECT 1
        FROM parent_chain
        WHERE id = :nodeId
    )
    """, nativeQuery = true)
    boolean existsInParentChain(Long spaceId, Long nodeId, Long parentId);

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

    @Query(value = """
    WITH RECURSIVE node_tree AS (
        SELECT id
        FROM storage_node
        WHERE id = :nodeId

        UNION ALL

        SELECT child.id
        FROM storage_node child
        JOIN node_tree parent ON child.parent_id = parent.id
    )
    SELECT sn.*
    FROM storage_node sn
    JOIN node_tree nt ON nt.id = sn.id
    """, nativeQuery = true)
    List<StorageNode> findAllWithRecursive(@Param("nodeId") Long nodeId);

    @Query(value = "SELECT pg_advisory_xact_lock(:spaceId)", nativeQuery = true)
    void lockSpace(Long spaceId);
}
