package org.bazar.bazarstorage.app.api.auth;

import org.bazar.authorization.sdk.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {

    /**
     * Разрешения для ресурса
     */
    Permission permission();

    /**
     * SpEL-выражение для получения идентификатора пространства
     */
    String spaceIdParam() default "#spaceId";
}
