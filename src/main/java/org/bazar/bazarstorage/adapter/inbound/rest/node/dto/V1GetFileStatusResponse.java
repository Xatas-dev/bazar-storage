package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import java.util.List;

public record V1GetFileStatusResponse(
        String status,
        AuthorResponse author,
        List<V1ErrorResponse> errors
) {
    public record V1ErrorResponse(
            String code,
            String description
    ) {
    }
}
