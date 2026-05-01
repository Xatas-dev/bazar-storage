package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import java.util.List;

public record V1GetNodesPaginationResponse(
        List<V1GetNodesResponse> content,
        Integer page,
        Integer pageSize,
        Long totalElements,
        Integer totalPages
) {
}
