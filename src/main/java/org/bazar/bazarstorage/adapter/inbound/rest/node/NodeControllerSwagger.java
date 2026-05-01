package org.bazar.bazarstorage.adapter.inbound.rest.node;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;

public interface NodeControllerSwagger {
    @Operation(summary = "Получить URL для загрузки файла", description = "Возвращает URL для загрузки файла в хранилище и UUID файла")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetUploadUrlResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetUploadUrlResponse getUploadUrl(@ParameterObject V1GetUploadUrlRequest request, String spaceId);

    @Operation(summary = "Получить статус загрузки файла", description = "Возвращает статус загрузки файла по его UUID")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetFileStatusResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetFileStatusResponse getFileStatus(@Parameter String fileUuid, String spaceId);

    @Operation(summary = "Получить все узлы хранилища по пространству", description = "Возвращает узлы хранилища, принадлежащие указанному пространству, с поддержкой пагинации")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetNodesPaginationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetNodesPaginationResponse getNodes(String spaceId, @ParameterObject Pageable pageable);

    @Operation(summary = "Получить URL для скачивания файла", description = "Возвращает URL для скачивания файла")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetNodesPaginationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetDownloadUrlResponse getUrlForDownload(String spaceId, @Parameter String fileUuid);
}
