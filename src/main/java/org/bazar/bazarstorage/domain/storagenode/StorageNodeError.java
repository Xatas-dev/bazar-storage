package org.bazar.bazarstorage.domain.storagenode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.bazar.bazarstorage.domain.DomainObject;

@Getter
@Setter
@Entity
@Table(name = "storage_node_error")
public class StorageNodeError extends DomainObject {
    @OneToOne(fetch = FetchType.LAZY)
    private StorageNode storageNode;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "description")
    private String description;
}
