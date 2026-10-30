package org.bazar.bazarstorage.it.controller;

import builder.JwtBuilder;
import builder.StorageNodeErrorBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1CreateNodeRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1CreateNodeResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1UpdateNodeRequest;
import org.bazar.bazarstorage.app.api.node.output.AuthorStatus;
import org.bazar.bazarstorage.app.api.node.output.NodeErrorInfo;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeUploadStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType.FILE_TOO_LARGE;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus.ACTIVE;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus.DELETED;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeType.FILE;
import static org.bazar.bazarstorage.domain.storagenode.StorageNodeType.FOLDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NodeControllerIntegrationTest extends AbstractControllerIntegrationTest {
    private static final String SPACE_ID = "1";
    private static final String VALID_NODE_NAME = "name.docx";
    private static final String INVALID_FILE_NAME =
            "fileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileNamefileN.exe";
    private static final Long VALID_SIZE = 5102L;
    private static final Long INVALID_SIZE = 13123123123123132L;
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String STORAGE_DOMAIN = "STORAGE";
    private static final String FILE_TOO_LARGE_MESSAGE = "Размер файла не может превышать 50 МБ";
    private static final String FILE_EXTENSION_NOT_ALLOWED_MESSAGE = "Невозможно загрузить файл с таким расширением";
    private static final String FILE_NAME_TOO_LARGE_MESSAGE = "Длина имени файла не может превышать 100 символов";
    private static final String PARENT_NODE_WRONG_SPACE_MESSAGE = "Родительский узел находится в другом пространстве";
    private static final String PARENT_NODE_WRONG_TYPE_MESSAGE = "Родительский узел имеет некорретный тип";
    private static final String PARENT_NODE_WRONG_STATUS_MESSAGE = "Родительский узел в неверном статусе";
    private static final TypeReference<V1CreateNodeResponse> TYPE_REF_V1_POST_CREATE_NODE_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<V1GetUploadUrlResponse> TYPE_REF_V1_GET_UPLOAD_URL_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<List<NodeErrorInfo>> TYPE_REF_V1_POST_UPLOAD_URL_RESPONSE_VALIDATION_ERROR = new TypeReference<>() {};
    private static final TypeReference<V1GetFileStatusResponse> TYPE_REF_V1_GET_FILE_STATUS_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<V1GetNodesPaginationResponse> TYPE_REF_V1_GET_NODES_PAGINATION_RESPONSE = new TypeReference<>() {};
    private static final TypeReference<V1GetDownloadUrlResponse> TYPE_REF_V1_GET_DOWNLOAD_URL_RESPONSE = new TypeReference<>() {};

    @BeforeEach
    void startUp() {
        wireMockTestHelper.startMockBazarPersonaServer();
        wireMockTestHelper.startMockBazarFilesServer();
        when(bazarAuthorizationClient.authorize(any())).thenReturn(true);
    }

    @Test
    @DisplayName("Успешное создание узла с типом Файл без родительского узла")
    void createNode_file_withoutParentNode_success() throws Exception {
        V1CreateNodeResponse result = restTestUtil.postPerform(
                String.format(POST_CREATE_NODE_API_URL, SPACE_ID),
                Map.of(),
                new V1CreateNodeRequest(VALID_NODE_NAME, VALID_SIZE, "FILE", null),
                TYPE_REF_V1_POST_CREATE_NODE_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertNotNull(result);
        assertNotNull(result.nodeId());
    }

    @Test
    @DisplayName("Успешное создание узла с типом Файл с родительским узлом")
    void createNode_file_withParentNode_success() throws Exception {
        StorageNode parent = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, Long.parseLong(SPACE_ID));

        V1CreateNodeResponse result = restTestUtil.postPerform(
                String.format(POST_CREATE_NODE_API_URL, SPACE_ID),
                Map.of(),
                new V1CreateNodeRequest(VALID_NODE_NAME, VALID_SIZE, "FILE", parent.getId()),
                TYPE_REF_V1_POST_CREATE_NODE_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertNotNull(result);
        assertNotNull(result.nodeId());
        StorageNode savedNode = storageNodeJpaRepository.findById(Long.parseLong(result.nodeId())).get();
        assertEquals(parent.getId(), savedNode.getParent().getId());
    }

    @Test
    @DisplayName("Успешное создание узла с типом Папка")
    void createNode_folder_success() throws Exception {
        V1CreateNodeResponse result = restTestUtil.postPerform(
                String.format(POST_CREATE_NODE_API_URL, SPACE_ID),
                Map.of(),
                new V1CreateNodeRequest(VALID_NODE_NAME, null, "FOLDER", null),
                TYPE_REF_V1_POST_CREATE_NODE_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertNotNull(result);
        assertNotNull(result.nodeId());
    }

    @Test
    @DisplayName("Ошибки валидации по созданию узла типа Файл")
    void createNode_file_validationError() throws Exception {
        StorageNode wrongParent = testDataHelper.createStorageNodeWith(DELETED, FILE, 5L);

        List<NodeErrorInfo> result = restTestUtil.postPerform(
                String.format(POST_CREATE_NODE_API_URL, SPACE_ID),
                Map.of(),
                new V1CreateNodeRequest(INVALID_FILE_NAME, INVALID_SIZE, "FILE", wrongParent.getId()),
                TYPE_REF_V1_POST_UPLOAD_URL_RESPONSE_VALIDATION_ERROR,
                Map.of(),
                status().isBadRequest()
        );

        List<String> stringResult = result.stream().map(NodeErrorInfo::description).toList();
        assertEquals(6, stringResult.size());
        assertTrue(stringResult.contains(FILE_EXTENSION_NOT_ALLOWED_MESSAGE));
        assertTrue(stringResult.contains(FILE_TOO_LARGE_MESSAGE));
        assertTrue(stringResult.contains(FILE_NAME_TOO_LARGE_MESSAGE));
        assertTrue(stringResult.contains(PARENT_NODE_WRONG_SPACE_MESSAGE));
        assertTrue(stringResult.contains(PARENT_NODE_WRONG_TYPE_MESSAGE));
        assertTrue(stringResult.contains(PARENT_NODE_WRONG_STATUS_MESSAGE));
    }

    @Test
    @DisplayName("Успешное получение URL для загрузки файла")
    void getNodeUploadUrl_success() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(FILE);
        wireMockTestHelper.stubBazarFilesInitiateUpload_200(
                storageNode.getNodeName(), String.valueOf(storageNode.getSize()), PDF_CONTENT_TYPE, STORAGE_DOMAIN,
                "/NodeControllerIntegrationTest/V1InitiateUploadResponseDto.json");

        V1GetUploadUrlResponse result = restTestUtil.getPerform(
                String.format(GET_UPLOAD_URL_API_URL, storageNode.getSpaceId(), storageNode.getId()),
                Map.of(),
                TYPE_REF_V1_GET_UPLOAD_URL_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertEquals("http://s3-test/files/d628bc48-a393-4b19-a640-2c16773e0dec", result.uploadUrl());
        StorageNode storageNodeAfter = storageNodeJpaRepository.findById(storageNode.getId()).get();
        assertEquals("d628bc48-a393-4b19-a640-2c16773e0dec", storageNodeAfter.getFileUuid().toString());
    }

    @Test
    @DisplayName("Неуспешное получение URL для загрузки файла - узел не найден")
    void getNodeUploadUrl_failed_storageNodeNotFound() throws Exception {
        restTestUtil.getPerform(
                String.format(GET_UPLOAD_URL_API_URL, 5L, 5L),
                Map.of(),
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isNotFound()
        );
    }

    @Test
    @DisplayName("Неуспешное получение URL для загрузки файла - у узла неверный тип")
    void getNodeUploadUrl_failed_storageNodeHasWrongType() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(FOLDER);

        restTestUtil.getPerform(
                String.format(GET_UPLOAD_URL_API_URL, storageNode.getSpaceId(), storageNode.getId()),
                Map.of(),
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isBadRequest()
        );
    }

    @Test
    @DisplayName("Получение статуса загрузки узла")
    void getFileStatus_success_UPLOADED() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(StorageNodeUploadStatus.UPLOADED);
        wireMockTestHelper.stubBazarPersonaGetUsers_200(
                List.of(JwtBuilder.TEST_USER_ID), "/NodeControllerIntegrationTest/PersonaGetUsersResponse.json");

        V1GetFileStatusResponse result = restTestUtil.getPerform(
                String.format(GET_STATUS_API_URL, SPACE_ID, storageNode.getId()),
                Map.of("fileUuid", storageNode.getFileUuid()),
                TYPE_REF_V1_GET_FILE_STATUS_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertEquals(StorageNodeUploadStatus.UPLOADED.name(), result.status());
        assertNotNull(result.author());
        assertEquals(AuthorStatus.EXIST.name(), result.author().status());
    }

    @Test
    @DisplayName("Получение статуса загрузки узла в процессе")
    void getFileStatus_success_IN_PROGRESS() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(StorageNodeUploadStatus.IN_PROGRESS);

        V1GetFileStatusResponse result = restTestUtil.getPerform(
                String.format(GET_STATUS_API_URL, SPACE_ID, storageNode.getId()),
                Map.of(),
                TYPE_REF_V1_GET_FILE_STATUS_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertEquals(StorageNodeUploadStatus.IN_PROGRESS.name(), result.status());
        assertNull(result.author());
    }

    @Test
    @DisplayName("Получение статуса загрузки узла с ошибками")
    void getFileStatus_success_ERROR() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(StorageNodeUploadStatus.ERROR, List.of(
                StorageNodeErrorBuilder.buildWith(FILE_TOO_LARGE.name()), StorageNodeErrorBuilder.buildDefault()
        ));

        V1GetFileStatusResponse result = restTestUtil.getPerform(
                String.format(GET_STATUS_API_URL, SPACE_ID, storageNode.getId()),
                Map.of(),
                TYPE_REF_V1_GET_FILE_STATUS_RESPONSE,
                Map.of(),
                status().isOk()
        );

        assertEquals(StorageNodeUploadStatus.ERROR.name(), result.status());
        assertNull(result.author());
        assertEquals(2, result.errors().size());
    }

    @Test
    @DisplayName("Неуспешное получение статуса загрузки узла - неверный тип узла")
    void getFileStatus_failed_wrongStorageNodeType() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(FOLDER);

        restTestUtil.getPerform(
                String.format(GET_STATUS_API_URL, SPACE_ID, storageNode.getId()),
                Map.of(),
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isBadRequest()
        );
    }

    @Test
    @DisplayName("Получение узлов хранилища по пространству")
    void getNodes_success() throws Exception {
        wireMockTestHelper.stubBazarPersonaGetUsers_200(
                List.of(JwtBuilder.TEST_USER_ID), "/NodeControllerIntegrationTest/PersonaGetUsersResponse.json");
        StorageNode firstNode = testDataHelper.createStorageNodeWith(ACTIVE);
        StorageNode secondNode = testDataHelper.createStorageNodeWith(ACTIVE);

        List<V1GetNodesResponse> result = restTestUtil.getPerform(
                String.format(GET_NODES_API_URL, SPACE_ID),
                Map.of(),
                TYPE_REF_V1_GET_NODES_PAGINATION_RESPONSE,
                Map.of(),
                status().isOk()
        ).content();

        assertEquals(2, result.size());
        V1GetNodesResponse firstResultNode = result.getFirst();
        assertEquals(secondNode.getNodeName(), firstResultNode.nodeName());
        assertEquals(secondNode.getId().toString(), firstResultNode.nodeId());
        V1GetNodesResponse secondResultNode = result.get(1);
        assertEquals(firstNode.getNodeName(), secondResultNode.nodeName());
        assertEquals(firstNode.getId().toString(), secondResultNode.nodeId());
    }

    @Test
    @DisplayName("Получение узлов хранилища по пространству и родителю")
    void getNodes_success_withParent() throws Exception {
        wireMockTestHelper.stubBazarPersonaGetUsers_200(
                List.of(JwtBuilder.TEST_USER_ID), "/NodeControllerIntegrationTest/PersonaGetUsersResponse.json");
        StorageNode parentNode = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER);
        StorageNode childNode1 = testDataHelper.createStorageNodeWith(ACTIVE, FILE, parentNode);
        StorageNode childNode2 = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, parentNode);
        testDataHelper.createStorageNodeWith(DELETED, FILE, parentNode);

        List<V1GetNodesResponse> result = restTestUtil.getPerform(
                String.format(GET_NODES_API_URL, SPACE_ID),
                Map.of("parentId", parentNode.getId()),
                TYPE_REF_V1_GET_NODES_PAGINATION_RESPONSE,
                Map.of(),
                status().isOk()
        ).content();

        assertEquals(2, result.size());
        V1GetNodesResponse firstResultNode = result.getFirst();
        assertEquals(childNode2.getId().toString(), firstResultNode.nodeId());
        V1GetNodesResponse secondResultNode = result.get(1);
        assertEquals(childNode1.getId().toString(), secondResultNode.nodeId());
    }

    @Test
    @DisplayName("Успешное получение presigned URL для скачивания файла")
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

    @Test
    @DisplayName("Неуспешное получение presigned URL для скачивания файла - некорректный тип узла")
    void getDownloadUrl_failed_wrongStorageNodeType() throws Exception {
        UUID fileUuid = UUID.randomUUID();
        StorageNode storageNode = testDataHelper.createStorageNodeWith(fileUuid, ACTIVE, FOLDER);

        restTestUtil.getPerform(
                String.format(GET_DOWNLOAD_URL_API_URL, SPACE_ID, storageNode.getId()),
                Map.of("fileUuid", fileUuid),
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isBadRequest()
        );
    }

    @Test
    @DisplayName("Неуспешное получение presigned URL для скачивания файла - некорректный статус узла")
    void getDownloadUrl_failed_wrongStorageNodeStatus() throws Exception {
        UUID fileUuid = UUID.randomUUID();
        StorageNode storageNode = testDataHelper.createStorageNodeWith(fileUuid, DELETED, FILE);

        restTestUtil.getPerform(
                String.format(GET_DOWNLOAD_URL_API_URL, SPACE_ID, storageNode.getId()),
                Map.of("fileUuid", fileUuid),
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isBadRequest()
        );
    }

    @Test
    @DisplayName("Удаление файла из пространства")
    void deleteNode_file_success() throws Exception {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(StorageNodeUploadStatus.UPLOADED);

        restTestUtil.deletePerform(
                String.format(DELETE_NODE_API_URL, SPACE_ID, storageNode.getId()),
                Map.of(),
                null,
                TYPE_REF_VOID_RESPONSE,
                Map.of(),
                status().isOk()
        );

        StorageNode deletedNode = storageNodeJpaRepository.findById(storageNode.getId()).orElse(null);
        assertNotNull(deletedNode);
        assertEquals(DELETED, deletedNode.getStatus());
    }

    @Test
    @DisplayName("Удаление папки с вложенными папками и файлами из пространства")
    void deleteNode_folder_success() throws Exception {
        StorageNode storageNodeToDelete =
                testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, Long.parseLong(SPACE_ID));
        StorageNode folderStorageNode =
                testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, Long.parseLong(SPACE_ID), storageNodeToDelete);
        testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, Long.parseLong(SPACE_ID), storageNodeToDelete);
        testDataHelper.createStorageNodeWith(ACTIVE, FILE, Long.parseLong(SPACE_ID), storageNodeToDelete);
        testDataHelper.createStorageNodeWith(ACTIVE, FILE, Long.parseLong(SPACE_ID), folderStorageNode);

        restTestUtil.deletePerform(
                String.format(DELETE_NODE_API_URL, SPACE_ID, storageNodeToDelete.getId()),
                Map.of(),
                null,
                TYPE_REF_VOID_RESPONSE,
                Map.of(),
                status().isOk()
        );

        List<StorageNode> deletedStorageNodes = storageNodeJpaRepository.findAllWithRecursive(storageNodeToDelete.getId());
        deletedStorageNodes.forEach(storageNode -> assertEquals(DELETED, storageNode.getStatus()));
    }

    @Test
    @DisplayName("Ошибка авторизации при загрузке файла")
    void createNode_unauthorized() throws Exception {
        when(bazarAuthorizationClient.authorize(any())).thenReturn(false);

        restTestUtil.postPerform(
                String.format(POST_CREATE_NODE_API_URL, SPACE_ID),
                Map.of(),
                new V1CreateNodeRequest(VALID_NODE_NAME, VALID_SIZE, null, null),
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isForbidden()
        );
    }

    @Test
    @DisplayName("Успешное обновление родительского узла")
    void updateNode_success() throws Exception {
        StorageNode parent = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER);
        StorageNode nodeToUpdate = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, parent);
        StorageNode newParent = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER);
        V1UpdateNodeRequest request = new V1UpdateNodeRequest();
        request.setParentId(newParent.getId());

        restTestUtil.patchPerform(
                String.format(PATCH_NODE_API_URL, SPACE_ID, nodeToUpdate.getId()),
                Map.of(),
                request,
                TYPE_REF_VOID_RESPONSE,
                Map.of(),
                status().isOk()
        );

        StorageNode nodeAfterUpdate = storageNodeJpaRepository.findById(nodeToUpdate.getId()).get();
        assertEquals(newParent.getId(), nodeAfterUpdate.getParent().getId());
    }

    @Test
    @DisplayName("Успешное обновление родительского узла - ничего не меняется")
    void updateNode_success_parentIdNullAndProvided() throws Exception {
        StorageNode parent = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER);
        StorageNode nodeToUpdate = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, parent);
        V1UpdateNodeRequest request = new V1UpdateNodeRequest();

        restTestUtil.patchPerform(
                String.format(PATCH_NODE_API_URL, SPACE_ID, nodeToUpdate.getId()),
                Map.of(),
                request,
                TYPE_REF_VOID_RESPONSE,
                Map.of(),
                status().isOk()
        );

        StorageNode nodeAfterUpdate = storageNodeJpaRepository.findById(nodeToUpdate.getId()).get();
        assertEquals(parent.getId(), nodeAfterUpdate.getParent().getId());
    }

    @Test
    @DisplayName("Неуспешное обновление родительского узла - некорректный тип родителя")
    void updateNode_failed_wrongParentType() throws Exception {
        StorageNode nodeToUpdate = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER);
        StorageNode parent = testDataHelper.createStorageNodeWith(ACTIVE, FILE);
        V1UpdateNodeRequest request = new V1UpdateNodeRequest();
        request.setParentId(parent.getId());

        restTestUtil.patchPerform(
                String.format(PATCH_NODE_API_URL, SPACE_ID, nodeToUpdate.getId()),
                Map.of(),
                request,
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isBadRequest()
        );
    }

    @Test
    @DisplayName("Неуспешное обновление родительского узла - создание цила")
    void updateNode_failed_cycle() throws Exception {
        StorageNode nodeToUpdate = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER);
        StorageNode child1 = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, nodeToUpdate);
        StorageNode child2 = testDataHelper.createStorageNodeWith(ACTIVE, FOLDER, child1);
        V1UpdateNodeRequest request = new V1UpdateNodeRequest();
        request.setParentId(child2.getId());

        restTestUtil.patchPerform(
                String.format(PATCH_NODE_API_URL, SPACE_ID, nodeToUpdate.getId()),
                Map.of(),
                request,
                TYPE_REF_STRING_RESPONSE,
                Map.of(),
                status().isConflict()
        );
    }
}
