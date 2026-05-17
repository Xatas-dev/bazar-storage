package org.bazar.bazarstorage.fw;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final int managementPort;
    private final int serverPort;

    public SecurityConfig(@Value("${management.server.port}") int managementPort, @Value("${server.port}") int serverPort) {
        this.managementPort = managementPort;
        this.serverPort = serverPort;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .securityMatcher(request -> request.getLocalPort() == serverPort)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz ->
                        authz.anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {
                        })
                );

        return http.build();
    }

    @Bean
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) {
        http
                .securityMatcher(request -> request.getLocalPort() == managementPort)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz ->
                        authz.anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
