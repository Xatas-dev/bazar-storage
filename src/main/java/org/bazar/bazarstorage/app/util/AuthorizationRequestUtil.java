package org.bazar.bazarstorage.app.util;

import lombok.RequiredArgsConstructor;
import org.bazar.authorization.sdk.AuthorizationRequest;
import org.bazar.authorization.sdk.Permission;
import org.bazar.bazarstorage.app.service.AuthenticationService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthorizationRequestUtil {
    private final AuthenticationService authenticationService;

    public AuthorizationRequest buildSimpleAuthorizationRequest(Long spaceId, Permission permission) {
        return AuthorizationRequest.builder()
                .spaceId(spaceId)
                .permission(permission)
                .bearerToken(authenticationService.getCurrentJwtToken())
                .build();
    }

    public AuthorizationRequest buildAuthorizationRequestWithAttributes(
            Long spaceId,
            Permission permission,
            Map<String, String> principalAttributes,
            Map<String, String> resourceAttributes
    ) {
        return AuthorizationRequest.builder()
                .spaceId(spaceId)
                .permission(permission)
                .principalAttributes(principalAttributes)
                .resourceAttributes(resourceAttributes)
                .bearerToken(authenticationService.getCurrentJwtToken())
                .build();
    }
}
