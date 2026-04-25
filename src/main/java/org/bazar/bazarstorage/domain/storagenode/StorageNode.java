package org.bazar.bazarstorage.domain.storagenode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.bazar.bazarstorage.domain.DomainObject;

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
}
