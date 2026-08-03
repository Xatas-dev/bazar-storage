package org.bazar.bazarstorage.domain.storagenode;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.bazar.bazarstorage.domain.DomainObject;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "storage_node")
public class StorageNode extends DomainObject {
    @Column(name = "space_id")
    private Long spaceId;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "file_uuid")
    private UUID fileUuid;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StorageNodeStatus status;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private StorageNodeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private StorageNode parent;

    @Column(name = "size")
    private Long size;

    @Column(name = "user_id")
    private UUID userId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "storage_node_errors", columnDefinition = "jsonb")
    private List<StorageNodeError> storageNodeErrors;
}
