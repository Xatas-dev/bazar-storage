package org.bazar.bazarstorage.app.impl.node.commands;

public record GetUploadUrlCommand(
        String fileName,
        Long size,
        String spaceId
) {}
