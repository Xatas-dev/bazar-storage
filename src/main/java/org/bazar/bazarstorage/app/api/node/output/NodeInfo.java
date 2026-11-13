package org.bazar.bazarstorage.app.api.node.output;

public record NodeInfo(
        String nodeId,
        String nodeName,
        Long size,
        String type,
        String uploadedAt,
        AuthorInfo author
) {
}
