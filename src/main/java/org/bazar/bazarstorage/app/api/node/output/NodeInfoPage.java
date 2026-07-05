package org.bazar.bazarstorage.app.api.node.output;

import java.util.List;

public record NodeInfoPage(
        List<NodeInfo> content,
        Integer page,
        Integer pageSize,
        Long totalElements,
        Integer totalPages
) {
}
