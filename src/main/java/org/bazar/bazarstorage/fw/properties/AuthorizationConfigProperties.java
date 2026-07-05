package org.bazar.bazarstorage.fw.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("service.authorization")
public record AuthorizationConfigProperties(
        String host,
        int port
) {}
