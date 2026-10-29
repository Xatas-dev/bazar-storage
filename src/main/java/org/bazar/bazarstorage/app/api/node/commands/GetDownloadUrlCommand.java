package org.bazar.bazarstorage.app.api.node.commands;

public record GetDownloadUrlCommand(
        Long spaceId,
        Long nodeId
) {}
