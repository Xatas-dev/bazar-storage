package org.bazar.bazarstorage.it.testutil;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.bazar.bazarstorage.it.testutil.TestDataTransformUtil.readFileWithoutThrow;

@Component
public class WireMockTestHelper {
    private static final String GET_USERS_BAZAR_PERSONA = "/users";
    private static final String INITIATE_UPLOAD_BAZAR_FILES = "/api/v1/files/initiate-upload";
    private static final String INITIATE_DOWNLOAD_BAZAR_FILES = "/api/v1/files/initiate-download";

    @Autowired
    protected WireMockServer bazarPersonaServer;
    @Autowired
    protected WireMockServer bazarFilesServer;

    public void startMockBazarPersonaServer() {
        bazarPersonaServer.start();
    }

    public void startMockBazarFilesServer() {
        bazarFilesServer.start();
    }

    public void stopWireMockServers() {
        bazarFilesServer.stop();
        bazarPersonaServer.stop();
    }

    public void stubBazarPersonaGetUsers_200(List<UUID> userIds, String bodyPath) {
        MappingBuilder mapping = get(urlPathEqualTo(GET_USERS_BAZAR_PERSONA));

        for (UUID id : userIds) {
            mapping = mapping.withQueryParam("ids", equalTo(id.toString()));
        }

        mapping.willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(readFileWithoutThrow(bodyPath))
        );

        bazarPersonaServer.stubFor(mapping);
    }

    public void stubBazarFilesInitiateUpload_200(String fileName, String size, String contentType, String domain, String bodyPath) {
        bazarFilesServer.stubFor(get(urlPathEqualTo(INITIATE_UPLOAD_BAZAR_FILES))
                .withQueryParam("fileName", equalTo(fileName))
                .withQueryParam("size", equalTo(size))
                .withQueryParam("contentType", equalTo(contentType))
                .withQueryParam("domain", equalTo(domain))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readFileWithoutThrow(bodyPath))));
    }

    public void stubBazarFilesInitiateDownload_200(String fileUuid, String bodyPath) {
        bazarFilesServer.stubFor(get(urlPathEqualTo(INITIATE_DOWNLOAD_BAZAR_FILES))
                .withQueryParam("fileUuid", equalTo(fileUuid))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readFileWithoutThrow(bodyPath))));
    }
}
