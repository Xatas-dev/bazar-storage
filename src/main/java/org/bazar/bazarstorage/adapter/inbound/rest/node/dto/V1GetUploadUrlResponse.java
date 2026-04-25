package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными для загрузки файла")
public record V1GetUploadUrlResponse(
        @Schema(description = "URL для загрузки в хранилище")
        String uploadUrl,

        @Schema(description = "UUID файла в bazar-files")
        String fileUuid
) {}
