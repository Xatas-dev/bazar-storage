package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

public record V1GetNodesResponse(
        String fileUuid,
        String fileName,
        Long size,
        String type,
        String uploadedAt,
        V1GetNodesAuthorResponse author
) {
}
