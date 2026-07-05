package org.bazar.bazarstorage.app.api.node.commands;

public record GetUploadUrlCommand(
        String fileName,
        Long size,
        String spaceId
) {}
