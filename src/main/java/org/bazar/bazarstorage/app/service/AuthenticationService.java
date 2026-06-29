package org.bazar.bazarstorage.app.service;

import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationService {
    public UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new BusinessException(ErrorCode.NOT_AUTHENTICATED);
        }
        return UUID.fromString(token.getToken().getSubject());
    }

    public String getCurrentJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new BusinessException(ErrorCode.NOT_AUTHENTICATED);
        }
        return token.getToken().getTokenValue();
    }
}
