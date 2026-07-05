package org.bazar.bazarstorage.app.api.auth;

import org.bazar.authorization.sdk.AuthorizationRequest;

public interface AuthorizationService {
    void authorize(AuthorizationRequest request);
}
