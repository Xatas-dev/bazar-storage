package org.bazar.bazarstorage.app.api.node.commands;

public record GetUploadUrlCommand(
        Long spaceId,
        Long nodeId
) {}
