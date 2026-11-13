package org.bazar.bazarstorage.app.api.node.commands;

import org.bazar.bazarstorage.domain.storagenode.StorageNodeType;

public record CreateNodeCommand(
        String name,
        Long size,
        Long spaceId,
        StorageNodeType type,
        Long parentId
) {}
