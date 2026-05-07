package org.bazar.bazarstorage.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {
    @Value("${service.bazar-files.port}")
    private int bazarFilesPort;

    @Value("${service.bazar-persona.port}")
    private int bazarPersonaPort;

    @Bean(name = "bazarFilesServer")
    public WireMockServer bazarFilesServer() {
        return new WireMockServer(bazarFilesPort);
    }

    @Bean(name = "bazarPersonaServer")
    public WireMockServer bazarPersonaServer() {
        return new WireMockServer(bazarPersonaPort);
    }
}
