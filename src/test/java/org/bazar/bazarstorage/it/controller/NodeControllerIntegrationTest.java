package org.bazar.bazarstorage.it.controller;

import builder.JwtBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NodeControllerIntegrationTest extends AbstractControllerIntegrationTest {
    private static final String SPACE_ID = "1";
    private static final String VALID_FILE_NAME = "fileName.docx";
    private static final String INVALID_FILE_NAME =
            "fileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileN.exe";
    private static final Long VALID_SIZE = 5102L;
    private static final Long INVALID_SIZE = 13123123123123132L;
    private static final String DOCX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String STORAGE_DOMAIN = "STORAGE";
    private static final String FILE_TOO_LARGE_MESSAGE = "Размер файла не может превышать 52428800 байт";
    private static final String FILE_EXTENSION_NOT_ALLOWED_MESSAGE = "Невозможно загрузить файл с расширением %s";
    private static final String FILE_NAME_TOO_LARGE_MESSAGE = "Длина имени файла не может превышать 100 символов";
    private static final TypeReference<V1GetUploadUrlResponse> TYPE_REF_V1_POST_UPLOAD_URL_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<List<String>> TYPE_REF_V1_POST_UPLOAD_URL_RESPONSE_VALIDATION_ERROR = new TypeReference<>() {};
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
                VALID_FILE_NAME, String.valueOf(VALID_SIZE), DOCX_CONTENT_TYPE, STORAGE_DOMAIN, "/NodeControllerIntegrationTest/V1InitiateUploadResponseDto.json"
        );

        V1GetUploadUrlResponse result = restTestUtil.postPerform(
                String.format(POST_UPLOAD_URL_API_URL, SPACE_ID),
                Map.of(),
                new V1GetUploadUrlRequest(VALID_FILE_NAME, VALID_SIZE),
                TYPE_REF_V1_POST_UPLOAD_URL_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertNotNull(result);
        assertNotNull(result.uploadUrl());
        assertNotNull(result.nodeId());
    }

    @Test
    @DisplayName("Ошибки валидации по получению presigned URL для загрузки файла")
    void getUploadUrl_validationError() throws Exception {
        List<String> result = restTestUtil.postPerform(
                String.format(POST_UPLOAD_URL_API_URL, SPACE_ID),
                Map.of(),
                new V1GetUploadUrlRequest(INVALID_FILE_NAME, INVALID_SIZE),
                TYPE_REF_V1_POST_UPLOAD_URL_RESPONSE_VALIDATION_ERROR,
                Map.of(),
                status().isBadRequest()
        );

        assertTrue(result.contains(String.format(FILE_EXTENSION_NOT_ALLOWED_MESSAGE, "exe")));
        assertTrue(result.contains(FILE_TOO_LARGE_MESSAGE));
        assertTrue(result.contains(FILE_NAME_TOO_LARGE_MESSAGE));
    }

    @Test
    @DisplayName("Получение статуса загруженного файла")
    void getFileStatus_success_UPLOADED() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(StorageNodeStatus.UPLOADED);
        wireMockTestHelper.stubBazarPersonaGetUsers_200(
                List.of(JwtBuilder.TEST_USER_ID), "/NodeControllerIntegrationTest/PersonaGetUsersResponse.json");

        V1GetFileStatusResponse result = restTestUtil.getPerform(
                String.format(GET_STATUS_API_URL, SPACE_ID, storageNode.getId()),
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
    @DisplayName("Получение статуса файла в процессе")
    void getFileStatus_success_IN_PROGRESS() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(StorageNodeStatus.IN_PROGRESS);

        V1GetFileStatusResponse result = restTestUtil.getPerform(
                String.format(GET_STATUS_API_URL, SPACE_ID, storageNode.getId()),
                Map.of(),
                TYPE_REF_V1_GET_FILE_STATUS_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertEquals(StorageNodeStatus.IN_PROGRESS.name(), result.status());
        assertNull(result.author());
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
        assertEquals(secondNode.getId().toString(), firstResultNode.nodeId());
        V1GetNodesResponse secondResultNode = result.get(1);
        assertEquals(firstNode.getNodeName(), secondResultNode.fileName());
        assertEquals(firstNode.getId().toString(), secondResultNode.nodeId());
    }

    @Test
    @DisplayName("Получение presigned URL для скачивания файла")
    void getDownloadUrl_success() throws Exception {
        UUID fileUuid = UUID.randomUUID();
        StorageNode storageNode = testDataHelper.createStorageNodeWith(fileUuid);
        wireMockTestHelper.stubBazarFilesInitiateDownload_200(
                fileUuid.toString(), "/NodeControllerIntegrationTest/V1InitiateDownloadResponseDto.json");

        V1GetDownloadUrlResponse result = restTestUtil.getPerform(
                String.format(GET_DOWNLOAD_URL_API_URL, SPACE_ID, storageNode.getId()),
                Map.of("fileUuid", fileUuid),
                TYPE_REF_V1_GET_DOWNLOAD_URL_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertNotNull(result);
        assertNotNull(result.downloadUrl());
    }
}
