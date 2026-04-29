package org.bazar.bazarstorage.app.impl.node.commands;

public record FileUploadResultCommand(
    String domain,
    Long size,
    String fileUuid,
    FileUploadStatus status
) {
}
