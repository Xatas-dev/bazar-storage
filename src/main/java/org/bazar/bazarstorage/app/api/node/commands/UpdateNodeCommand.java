package org.bazar.bazarstorage.app.api.node.commands;

public record UpdateNodeCommand(
        Long nodeId,
        Long spaceId,
        Long parentId,
        boolean parentIdProvided
) {}
