package org.bazar.bazarstorage.app.api.node.commands;

public record FileUploadResultCommand(
    String domain,
    Long size,
    String fileUuid,
    FileUploadStatus status
) {
}
