package org.bazar.bazarstorage.adapter.inbound.rest.node;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.springdoc.core.annotations.ParameterObject;

public interface NodeControllerSwagger {
    @Operation(summary = "Получить URL для загрузки файла", description = "Возвращает URL для загрузки файла в хранилище и UUID файла")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = V1GetUploadUrlResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    V1GetUploadUrlResponse getUploadUrl(@ParameterObject V1GetUploadUrlRequest request);
}
