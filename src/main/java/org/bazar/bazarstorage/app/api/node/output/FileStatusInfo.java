package org.bazar.bazarstorage.app.api.node.output;

import java.util.List;

public record FileStatusInfo(
        String status,
        AuthorInfo author,
        List<NodeErrorInfo> errors
) {
}
