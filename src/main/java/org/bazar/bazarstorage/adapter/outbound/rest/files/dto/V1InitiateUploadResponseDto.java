package org.bazar.bazarstorage.adapter.outbound.rest.files.dto;

public record V1InitiateUploadResponseDto(
        String uploadUrl,
        String fileUuid
) {
}
