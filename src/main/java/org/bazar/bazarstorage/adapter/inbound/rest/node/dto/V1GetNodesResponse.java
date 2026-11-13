package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

public record V1GetNodesResponse(
        String nodeId,
        String nodeName,
        Long size,
        String type,
        String uploadedAt,
        AuthorResponse author
) {
}
