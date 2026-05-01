package org.bazar.bazarstorage.app.impl.node.output;

public record AuthorInfo(
        String firstName,
        String lastName,
        AuthorStatus status
) {
}
