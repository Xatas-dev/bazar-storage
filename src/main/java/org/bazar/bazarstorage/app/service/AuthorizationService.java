package org.bazar.bazarstorage.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.authorization.sdk.AuthorizationRequest;
import org.bazar.authorization.sdk.BazarAuthorizationClient;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.AUTHORIZATION_ERROR;
import static org.bazar.bazarstorage.app.api.exception.ErrorCode.FORBIDDEN;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final BazarAuthorizationClient bazarAuthorizationClient;
    private final AuthenticationService authenticationService;

    public void authorize(AuthorizationRequest request) {
        boolean isAuthorized;
        try {
            isAuthorized = bazarAuthorizationClient.authorize(request);
        } catch (Exception ex) {
            log.error("Error while calling auth service for spaceId={}", request.getSpaceId(), ex);
            throw new BusinessException(AUTHORIZATION_ERROR);
        }

        if (!isAuthorized) {
            log.warn("Authorization denied for user={}, space={}, resource={}, action={}",
                    authenticationService.getAuthenticatedUserId(),
                    request.getSpaceId(),
                    request.getPermission().getResource(),
                    request.getPermission().getAction());
            throw new BusinessException(FORBIDDEN);
        }

        log.debug("Authorization granted for user={}, space={}, resource={}, action={}",
                authenticationService.getAuthenticatedUserId(),
                request.getSpaceId(),
                request.getPermission().getResource(),
                request.getPermission().getAction());
    }
}
