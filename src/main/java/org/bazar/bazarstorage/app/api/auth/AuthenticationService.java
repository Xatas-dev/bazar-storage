package org.bazar.bazarstorage.app.api.auth;

import java.util.UUID;

public interface AuthenticationService {
    UUID getAuthenticatedUserId();

    String getCurrentJwtToken();
}
