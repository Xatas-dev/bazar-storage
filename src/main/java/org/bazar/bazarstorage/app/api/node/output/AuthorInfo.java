package org.bazar.bazarstorage.app.api.node.output;

public record AuthorInfo(
        String firstName,
        String lastName,
        AuthorStatus status
) {
}
