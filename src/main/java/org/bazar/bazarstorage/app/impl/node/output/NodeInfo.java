package org.bazar.bazarstorage.app.impl.node.output;

public record NodeInfo(
        String nodeId,
        String fileName,
        Long size,
        String type,
        String uploadedAt,
        AuthorInfo author
) {
}
