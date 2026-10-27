package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record V1GetUploadUrlResponse(
        @Schema(description = "URL для загрузки в хранилище")
        String uploadUrl
) {}
