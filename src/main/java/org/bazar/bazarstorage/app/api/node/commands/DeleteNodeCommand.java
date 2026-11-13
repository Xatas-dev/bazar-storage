package org.bazar.bazarstorage.app.api.node.commands;

public record DeleteNodeCommand(
        Long spaceId,
        Long nodeId
) {}
