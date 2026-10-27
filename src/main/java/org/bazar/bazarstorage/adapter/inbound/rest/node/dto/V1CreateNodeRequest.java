package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на создание узла")
public record V1CreateNodeRequest(
        @Schema(description = "Имя узла")
        String name,

        @Schema(description = "Размер файла")
        Long size,

        @Schema(description = "Тип узла")
        String type,

        @Schema(description = "Идентификатор родительского узла")
        Long parentId
) {}
