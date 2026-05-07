package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

public record V1GetFileStatusResponse(
        String status,
        AuthorResponse author
) {
}
