package org.bazar.bazarstorage.adapter.inbound.rest.node;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1CreateNodeRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1CreateNodeResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;

public interface NodeControllerSwagger {
    @Operation(summary = "Создать узел в пространстве", description = "Возвращает информацию о созданном узле")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1CreateNodeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1CreateNodeResponse createNode(V1CreateNodeRequest request, String spaceId);

    @Operation(summary = "Получение URL для загрузки файла в S3", description = "Возвращает подписанную ссылку на загрузку файла в S3")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetUploadUrlResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetUploadUrlResponse getNodeUploadUrl(String spaceId, String nodeId);

    @Operation(summary = "Получить статус загрузки файла", description = "Возвращает статус загрузки файла по его UUID")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetFileStatusResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetFileStatusResponse getFileStatus(String spaceId, String nodeId);

    @Operation(summary = "Получить все узлы хранилища по пространству", description = "Возвращает узлы хранилища, принадлежащие указанному пространству, с поддержкой пагинации")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetNodesPaginationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetNodesPaginationResponse getNodes(String spaceId, @ParameterObject Pageable pageable);

    @Operation(summary = "Получить URL для скачивания файла", description = "Возвращает URL для скачивания файла")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetNodesPaginationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetDownloadUrlResponse getUrlForDownload(String spaceId, String nodeId);

    @Operation(summary = "Удалить узел из пространства", description = "Возвращает успешный ответ при удалении файла")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    void deleteNode(String spaceId, String nodeId);
}
