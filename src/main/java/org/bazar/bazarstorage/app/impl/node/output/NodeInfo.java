package org.bazar.bazarstorage.app.impl.node.output;

public record NodeInfo(
        String fileUuid,
        String fileName,
        Long size,
        String type,
        String uploadedAt,
        AuthorInfo author
) {
}
