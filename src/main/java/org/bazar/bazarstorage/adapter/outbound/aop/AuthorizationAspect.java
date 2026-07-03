package org.bazar.bazarstorage.adapter.outbound.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.bazar.authorization.sdk.AuthorizationRequest;
import org.bazar.bazarstorage.app.api.auth.AuthenticationService;
import org.bazar.bazarstorage.app.api.auth.AuthorizationService;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAspect {
    private final AuthorizationService authorizationService;
    private final AuthenticationService authenticationService;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Before("@annotation(authorize)")
    public void checkAuthorization(JoinPoint joinPoint, Authorize authorize) {
        Long spaceId = getSpaceId(joinPoint, authorize.spaceIdParam());
        AuthorizationRequest request = AuthorizationRequest.builder()
                .spaceId(spaceId)
                .permission(authorize.permission())
                .bearerToken(authenticationService.getCurrentJwtToken())
                .build();

        authorizationService.authorize(request);
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private Long getSpaceId(JoinPoint joinPoint, String expression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        EvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        Object value = parser.parseExpression(expression).getValue(context);

        return switch (value) {
            case null -> throw new IllegalArgumentException(String.format("Expression '%s' returned null", expression));
            case Long l -> l;
            case String s -> Long.parseLong(s);
            default -> throw new IllegalArgumentException(
                    String.format("Expression '%s' returned unsupported type %s",
                            expression,
                            value.getClass().getName()));
        };

    }
}
