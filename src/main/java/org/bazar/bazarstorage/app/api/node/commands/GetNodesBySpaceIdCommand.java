package org.bazar.bazarstorage.app.api.node.commands;

import org.springframework.data.domain.Pageable;

public record GetNodesBySpaceIdCommand(
        Long spaceId,
        Pageable pageable
) {
}
