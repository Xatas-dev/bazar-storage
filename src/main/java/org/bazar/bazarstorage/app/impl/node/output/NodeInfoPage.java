package org.bazar.bazarstorage.app.impl.node.output;

import java.util.List;

public record NodeInfoPage(
        List<NodeInfo> content,
        Integer page,
        Integer pageSize,
        Long totalElements,
        Integer totalPages
) {
}
