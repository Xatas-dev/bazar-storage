package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

public record AuthorResponse(
        String displayName,
        String avatarUrl,
        String status
) {
}
