package org.bazar.bazarstorage.app.impl.node.commands;

import org.springframework.data.domain.Pageable;

public record GetNodesBySpaceIdCommand(
        Long spaceId,
        Pageable pageable
) {
}
