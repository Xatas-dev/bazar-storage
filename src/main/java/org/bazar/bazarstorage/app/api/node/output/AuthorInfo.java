package org.bazar.bazarstorage.app.api.node.output;

public record AuthorInfo(
        String displayName,
        String avatarUrl,
        AuthorStatus status
) {
}
