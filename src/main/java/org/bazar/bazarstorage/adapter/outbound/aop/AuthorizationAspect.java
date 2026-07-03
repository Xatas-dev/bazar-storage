package org.bazar.bazarstorage.adapter.outbound.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.bazar.authorization.sdk.AuthorizationRequest;
import org.bazar.authorization.sdk.BazarAuthorizationClient;
import org.bazar.bazarstorage.adapter.inbound.rest.aop.Authorize;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.InternalException;
import org.bazar.bazarstorage.app.service.AuthenticationService;
import org.bazar.bazarstorage.app.service.AuthorizationService;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.*;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAspect {
    private final BazarAuthorizationClient bazarAuthorizationClient;
    private final AuthorizationService authorizationService;
    private final AuthenticationService authenticationService;

    @Before("@annotation(authorize)")
    public void checkAuthorization(JoinPoint joinPoint, Authorize authorize) {
        Long spaceId = getSpaceId(joinPoint, authorize.spaceIdParam());
        AuthorizationRequest request = AuthorizationRequest.builder()
                .spaceId(spaceId)
                .permission(authorize.permission())
                .bearerToken(authenticationService.getCurrentJwtToken())
                .build();

        boolean isAllowed;
        try {
            isAllowed = bazarAuthorizationClient.authorize(request);
        } catch (Exception e) {
            log.error("Error while calling auth service for spaceId: {}", spaceId, e);
            throw new InternalException(e.getMessage());
        }

        if (!isAllowed) {
            log.error("Auth denied: permission {}, user {}, space {}", authorize.permission(),
                    authenticationService.getAuthenticatedUserId(),
                    spaceId);
            throw new BusinessException(FORBIDDEN);
        }
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private Long getSpaceId(JoinPoint joinPoint, String spaceIdParam) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Long spaceId;
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(spaceIdParam)) {
                spaceId = Long.valueOf((String) args[i]);
                return spaceId;
            }
        }

        throw new IllegalArgumentException(String.format("%s param is not found!", spaceIdParam));
    }
}
