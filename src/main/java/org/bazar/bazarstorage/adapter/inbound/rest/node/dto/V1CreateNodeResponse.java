package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными для загрузки файла")
public record V1CreateNodeResponse(
        @Schema(description = "Идентификатор созданного узла")
        String nodeId
) {}
