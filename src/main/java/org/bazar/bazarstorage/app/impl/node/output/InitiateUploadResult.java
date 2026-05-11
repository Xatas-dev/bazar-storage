package org.bazar.bazarstorage.app.impl.node.output;

public record InitiateUploadResult(
        String uploadUrl,
        String fileUuid
) {
}
