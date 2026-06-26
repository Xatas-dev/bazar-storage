package org.bazar.bazarstorage.fw;

import lombok.RequiredArgsConstructor;
import org.bazar.authorization.sdk.BazarAuthorizationClient;
import org.bazar.bazarstorage.fw.properties.AuthorizationConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class AuthorizationConfig {
    private final AuthorizationConfigProperties properties;

    @Bean
    public BazarAuthorizationClient bazarAuthorizationClient() {
        return BazarAuthorizationClient.builder()
                .host(properties.host())
                .port(properties.port())
                .build();
    }
}
