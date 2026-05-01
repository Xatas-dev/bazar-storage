package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на получение URL для загрузки файла")
public record V1GetUploadUrlRequest(
        @Schema(description = "Оригинальное имя файла")
        String fileName,

        @Schema(description = "Размер файла")
        Long size
) {}
