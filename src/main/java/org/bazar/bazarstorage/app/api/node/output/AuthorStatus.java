package org.bazar.bazarstorage.app.api.node.output;

import org.bazar.bazarstorage.domain.user.User;

public enum AuthorStatus {
    EXIST,
    UNKNOWN;

    public static AuthorStatus from(User user) {
        return user != null ? EXIST : UNKNOWN;
    }
}
