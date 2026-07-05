package org.bazar.bazarstorage.app.api.node.output;

public record InitiateUploadResult(
        String uploadUrl,
        String fileUuid
) {
}
