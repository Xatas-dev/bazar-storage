package org.bazar.bazarstorage.app.api.node.commands;

import java.util.List;

public record FileUploadResultCommand(
    String domain,
    Long size,
    String fileUuid,
    FileUploadStatus status,
    String fileName,
    String contentType,
    List<Error> errors
) {
    public record Error(
            String code,
            String description
    ) {
    }
}
