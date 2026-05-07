package org.bazar.bazarstorage.it.controller;

import builder.JwtBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.app.impl.node.output.AuthorStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NodeControllerIntegrationTest extends AbstractControllerIntegrationTest {
    private static final String SPACE_ID = "1";
    private static final String VALID_FILE_NAME = "fileName.docx";
    private static final String INVALID_FILE_NAME =
            "fileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileN.exe";
    private static final String VALID_SIZE = "5102";
    private static final String INVALID_SIZE = "13123123123123132";
    private static final String DOCX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String STORAGE_DOMAIN = "STORAGE";
    private static final String FILE_TOO_LARGE_MESSAGE = "Размер файла не может превышать 52428800 байт";
    private static final String FILE_EXTENSION_NOT_ALLOWED_MESSAGE = "Невозможно загрузить файл с расширением %s";
    private static final String FILE_NAME_TOO_LARGE_MESSAGE = "Длина имени файла не может превышать 100 символов";
    private static final TypeReference<V1GetUploadUrlResponse> TYPE_REF_V1_GET_UPLOAD_URL_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<List<String>> TYPE_REF_V1_GET_UPLOAD_URL_RESPONSE_VALIDATION_ERROR = new TypeReference<>() {};
    private static final TypeReference<V1GetFileStatusResponse> TYPE_REF_V1_GET_FILE_STATUS_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<V1GetNodesPaginationResponse> TYPE_REF_V1_GET_NODES_PAGINATION_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<V1GetDownloadUrlResponse> TYPE_REF_V1_GET_DOWNLOAD_URL_RESPONSE = new TypeReference<>() {};

    @BeforeEach
    void startUp() {
        wireMockTestHelper.startMockBazarPersonaServer();
        wireMockTestHelper.startMockBazarFilesServer();
    }

    @Test
    @DisplayName("Успешное получение presigned URL для загрузки файла")
    void getUploadUrl_success() throws Exception {
        wireMockTestHelper.stubBazarFilesInitiateUpload_200(
                VALID_FILE_NAME, VALID_SIZE, DOCX_CONTENT_TYPE, STORAGE_DOMAIN, "/NodeControllerIntegrationTest/V1InitiateUploadResponseDto.json"
        );

        V1GetUploadUrlResponse result = restTestUtil.getPerform(
                String.format(GET_UPLOAD_URL_API_URL, SPACE_ID),
                Map.of("fileName", VALID_FILE_NAME,
                        "size", VALID_SIZE),
                TYPE_REF_V1_GET_UPLOAD_URL_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertNotNull(result);
        assertNotNull(result.uploadUrl());
        assertNotNull(result.fileUuid());
    }

    @Test
    @DisplayName("Ошибки валидации по получению presigned URL для загрузки файла")
    void getUploadUrl_validationError() throws Exception {
        List<String> result = restTestUtil.getPerform(
                String.format(GET_UPLOAD_URL_API_URL, SPACE_ID),
                Map.of("fileName", INVALID_FILE_NAME,
                        "size", INVALID_SIZE),
                TYPE_REF_V1_GET_UPLOAD_URL_RESPONSE_VALIDATION_ERROR,
                Map.of(),
                status().isBadRequest()
        );

        assertTrue(result.contains(String.format(FILE_EXTENSION_NOT_ALLOWED_MESSAGE, "exe")));
        assertTrue(result.contains(FILE_TOO_LARGE_MESSAGE));
        assertTrue(result.contains(FILE_NAME_TOO_LARGE_MESSAGE));
    }

    @Test
    @DisplayName("Получение статуса файла")
    void getFileStatus_success() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(StorageNodeStatus.UPLOADED);
        wireMockTestHelper.stubBazarPersonaGetUsers_200(
                List.of(JwtBuilder.TEST_USER_ID), "/NodeControllerIntegrationTest/PersonaGetUsersResponse.json");

        V1GetFileStatusResponse result = restTestUtil.getPerform(
                String.format(GET_STATUS_API_URL, SPACE_ID),
                Map.of("fileUuid", storageNode.getFileUuid()),
                TYPE_REF_V1_GET_FILE_STATUS_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertEquals(StorageNodeStatus.UPLOADED.name(), result.status());
        assertNotNull(result.author());
        assertEquals(AuthorStatus.EXIST.name(), result.author().status());
    }

    @Test
    @DisplayName("Получение узлов хранилища по пространству")
    void getNodes_success() throws Exception {
        wireMockTestHelper.stubBazarPersonaGetUsers_200(
                List.of(JwtBuilder.TEST_USER_ID), "/NodeControllerIntegrationTest/PersonaGetUsersResponse.json");
        StorageNode firstNode = testDataHelper.createStorageNodeWith(StorageNodeStatus.UPLOADED);
        StorageNode secondNode = testDataHelper.createStorageNodeWith(StorageNodeStatus.UPLOADED);

        List<V1GetNodesResponse> result = restTestUtil.getPerform(
                String.format(GET_NODES_API_URL, SPACE_ID),
                Map.of(),
                TYPE_REF_V1_GET_NODES_PAGINATION_RESPONSE,
                Map.of(),
                status().isOk()
        ).content();

        assertEquals(2, result.size());
        V1GetNodesResponse firstResultNode = result.getFirst();
        assertEquals(secondNode.getNodeName(), firstResultNode.fileName());
        assertEquals(secondNode.getFileUuid().toString(), firstResultNode.fileUuid());
        V1GetNodesResponse secondResultNode = result.get(1);
        assertEquals(firstNode.getNodeName(), secondResultNode.fileName());
        assertEquals(firstNode .getFileUuid().toString(), secondResultNode.fileUuid());
    }

    @Test
    @DisplayName("Получение presigned URL для скачивания файла")
    void getDownloadUrl_success() throws Exception {
        UUID fileUuid = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(fileUuid);
        wireMockTestHelper.stubBazarFilesInitiateDownload_200(
                fileUuid.toString(), "/NodeControllerIntegrationTest/V1InitiateDownloadResponseDto.json");

        V1GetDownloadUrlResponse result = restTestUtil.getPerform(
                String.format(GET_DOWNLOAD_URL_API_URL, SPACE_ID),
                Map.of("fileUuid", fileUuid),
                TYPE_REF_V1_GET_DOWNLOAD_URL_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertNotNull(result);
        assertNotNull(result.downloadUrl());
    }
}
