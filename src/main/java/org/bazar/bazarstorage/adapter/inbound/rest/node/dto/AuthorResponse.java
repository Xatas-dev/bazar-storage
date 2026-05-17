package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

public record AuthorResponse(
        String firstName,
        String lastName,
        String status
) {
}
